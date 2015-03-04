package io.github.devmop.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import io.github.devmop.actors.*;
import io.github.devmop.application.*;
import io.github.devmop.events.*;
import io.github.devmop.users.RegistrationRequest;


public class Http implements Listener, HttpHandler {

  private static final ObjectMapper mapper = new ObjectMapper();
  private final HttpServer server = initialise();
  private final EventBus bus;

  public Http(EventBus bus) {
    server.createContext("/users", this);
    this.bus = bus;
  }

  private HttpServer initialise() {
    try {
      return HttpServer.create(new InetSocketAddress(4040), 100);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handle(final HttpExchange httpExchange) {
    if ("POST".equals(httpExchange.getRequestMethod())) {
      sendRegistration(httpExchange);
    }
    else {
      send404(httpExchange);
    }
  }

  private void sendRegistration(final HttpExchange httpExchange) {
    try {
      Map<String, String> json = mapper.readValue(httpExchange.getRequestBody(), Map.class);
      bus.send(Message.create(new RegistrationRequest(json.get("email"), json.get("password"),
          new JsonResponse<>(new HttpResponse(httpExchange)))));
    }
    catch (IOException e) {
      httpExchange.close();
      //Do something
    }
  }

  @Override
  public void receive(final Message message) {
    if (message.content instanceof Start) {
      server.start();
    }
    if (message.content instanceof Terminate) {
      server.stop(1);
    }
  }

  private void send404(final HttpExchange httpExchange) {
    try {
      httpExchange.sendResponseHeaders(404, 1);
      httpExchange.close();
    }
    catch (IOException e) {
      //Don't care
    }
  }

  private static class HttpResponse implements Response<String> {

    private final HttpExchange exchange;

    HttpResponse(final HttpExchange exchange) {
      this.exchange = exchange;
    }

    @Override
    public void send(final String s) {
      try {
        byte[] bytes = s.getBytes(Charset.forName("UTF-8"));
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().flush();
      }
      catch (IOException e) {
        //Don't care client has probably gone
      }
      finally {
        exchange.close();
      }
    }
  }
}
