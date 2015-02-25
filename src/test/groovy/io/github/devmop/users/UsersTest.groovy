package io.github.devmop.users
import io.github.devmop.actors.Actor
import io.github.devmop.actors.IdResponse
import io.github.devmop.application.System
import org.junit.Test

import static io.github.devmop.application.Subsystem.AUTHENTICATION
import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat

class UsersTest implements Actor {

  def bus = System.initialise(AUTHENTICATION)
  def events = []

  @Test
  public void acceptedRegistrationsGenerateResponse() throws Exception {
    def response = new IdResponse<Registration>()

    bus.send(new RegistrationRequest('email@example.com', 'password', response))

    assertThat(response.response.id, notNullValue())
    assertThat(response.response.email, equalTo('email@example.com'))
  }

  @Test
  public void acceptedRegistrationsBroadcastEvent() throws Exception {
    bus.register(this)

    bus.send(new RegistrationRequest('email@example.com', 'password', new IdResponse<Registration>()))

    assertThat(events, not(empty()))

    assertThat(events[0].id, notNullValue())
    assertThat(events[0].email, equalTo('email@example.com'))
  }

  @Override
  void receive(final Object message) {
    if (message instanceof Registration) {
      events.add(message)
    }
  }
}