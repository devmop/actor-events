package io.github.devmop.users;

import java.util.*;

import io.github.devmop.actors.*;
import io.github.devmop.application.System;
import io.github.devmop.events.EventBus;
import io.github.devmop.events.Listener;
import io.github.devmop.events.Message;
import org.junit.Test;

import static io.github.devmop.application.Subsystem.AUTHENTICATION;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class UsersTest implements Listener {

  private EventBus bus = System.initialise(AUTHENTICATION);
  private List<Registration> events = new LinkedList<>();

  @Test
  public void acceptedRegistrationsGenerateResponse() throws Exception {
    IdResponse<Registration> response = new IdResponse<>();

    bus.send(Message.create(new RegistrationRequest("email@example.com", "password", response)));

    assertThat(response.response.id, notNullValue());
    assertThat(response.response.email, equalTo("email@example.com"));
  }

  @Test
  public void acceptedRegistrationsBroadcastEvent() throws Exception {
    bus.register(this);

    bus.send(Message.create(new RegistrationRequest("email@example.com", "password", new IdResponse<Registration>())));

    assertThat(events, not(empty()));

    assertThat(events.get(0).id, notNullValue());
    assertThat(events.get(0).email, equalTo("email@example.com"));
  }

  @Override
  public void receive(final Message message) {
    if (message.content instanceof Registration) {
      events.add((Registration) message.content);
    }
  }
}