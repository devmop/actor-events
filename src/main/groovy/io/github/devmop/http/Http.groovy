package io.github.devmop.http

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonSlurper
import io.github.devmop.actors.Actor
import io.github.devmop.actors.JsonResponse
import io.github.devmop.actors.Response
import io.github.devmop.application.Start
import io.github.devmop.application.Terminate
import io.github.devmop.events.EventBus
import io.github.devmop.users.Registration
import io.github.devmop.users.RegistrationRequest

import java.nio.charset.Charset


class Http implements Actor, HttpHandler {

  private final HttpServer server = HttpServer.create(new InetSocketAddress(4040), 100);
  private final EventBus bus;

  Http(EventBus bus) {
    server.createContext('/users', this)
    this.bus = bus
  }

  @Override
  void handle(final HttpExchange httpExchange) {
    if (httpExchange.requestMethod == 'POST') {
      def result = new JsonSlurper().parse(httpExchange.getRequestBody())
      bus.send(new RegistrationRequest(result.email,
          result.password,
          new JsonResponse<Registration>(new HttpResponse(httpExchange))))
    }
    else {
      httpExchange.close()
    }
  }

  @Override
  void receive(final Object message) {
    if (message instanceof Start) {
      server.start()
    }
    if (message instanceof Terminate) {
      server.stop(1)
    }
  }

  private static class HttpResponse implements Response<String> {

    private final HttpExchange exchange

    HttpResponse(final HttpExchange exchange) {
      this.exchange = exchange
    }

    @Override
    void send(final String s) {
      try {
        def bytes = s.getBytes(Charset.forName('UTF-8'))
        exchange.sendResponseHeaders(200, bytes.length)
        exchange.responseBody.write(bytes)
        exchange.responseBody.flush()
      }
      finally {
        exchange.close()
      }
    }
  }
}
