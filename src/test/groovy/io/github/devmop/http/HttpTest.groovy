package io.github.devmop.http

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import io.github.devmop.actors.Actor
import io.github.devmop.application.Subsystem
import io.github.devmop.application.System
import io.github.devmop.events.EventBus
import io.github.devmop.users.Registration
import io.github.devmop.users.RegistrationRequest
import org.junit.After
import org.junit.Before
import org.junit.Test

import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static io.github.devmop.application.Subsystem.HTTP
import static io.github.devmop.application.System.initialise
import static io.github.devmop.application.System.shutdown
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

class HttpTest implements Actor {

  private EventBus bus = initialise(HTTP)

  private messages = []

  @Before
  public void setup() throws Exception {
    bus.register(this)
  }

  @After
  public void shutdown() throws Exception {
    shutdown(bus)
  }

  @Test
  public void interpretsJson() throws Exception {
    def builder = new HTTPBuilder('http://localhost:4040')

    builder.request(POST, ContentType.JSON) {
      uri.path = '/users'
      body = [email: 'email@example.com', password: 'password']

      response.failure = {
        fail()
      }
    }

    assertThat(messages, notNullValue())
    assertThat(messages[0].email, equalTo('email@example.com'))
    assertThat(messages[0].password, equalTo('password'))
  }

  @Test
  public void receivesJsonResponse() throws Exception {
    def builder = new HTTPBuilder('http://localhost:4040')

    builder.request(POST, ContentType.JSON) {
      uri.path = '/users'
      body = [email: 'email@example.com', password: 'password']

      response.success = { resp, json ->
        assertThat(json.email, equalTo('email@example.com'))
        assertThat(json.id, equalTo('1'))
      }
      response.failure = {
        fail()
      }
    }
  }

  @Override
  void receive(final Object message) {
    if (message instanceof RegistrationRequest) {
      message.reply(new Registration('1', message.email))
      messages.add(message)
    }
  }
}