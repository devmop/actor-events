package io.github.devmop.http;
import java.util.*;

import static io.github.devmop.application.Subsystem.*;
import static io.github.devmop.application.System.*;
import io.github.devmop.events.EventBus;
import io.github.devmop.users.Registration;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.junit.*;

public class HttpTest {

  private EventBus bus = initialise(HTTP, AUTHENTICATION);

  private List<Registration> messages = new LinkedList<>();

  private CloseableHttpClient client = HttpClients.createDefault();

  @After
  public void tearDown() throws Exception {
    shutdown(bus);
    client.close();
  }

  @Test
  public void receivesJsonResponse() throws Exception {
    HttpUriRequest request = RequestBuilder.post()
        .setUri("http://localhost:4040/users")
        .setHeader("Content-Type", "applicaton/json")
        .setEntity(new StringEntity("{\"email\":\"email@example.com\", \"password\": \"password\"}")).build();

    CloseableHttpResponse response = client.execute(request);

    assert response.getStatusLine().getStatusCode() ==  200;
//
//    builder.request(POST, ContentType.JSON) {
//      uri.path = "/users"
//      body = [email: "email@example.com", password: "password"]
//
//      response.success = { resp, json ->
//        assertThat(json.email, equalTo("email@example.com"))
//        assertThat(json.id, equalTo("1"))
//      }
//      response.failure = {
//        fail()
//      }
//    }
  }
}