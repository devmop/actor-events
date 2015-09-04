package io.github.devmop.users;

import com.google.common.eventbus.*;
import io.github.devmop.actors.IdResponse;
import io.github.devmop.application.System;
import java.util.*;
import org.junit.Test;
import static io.github.devmop.application.Subsystem.AUTHENTICATION;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class UsersTest {

  private EventBus bus = System.initialise(AUTHENTICATION);
  private List<Registration> events = new LinkedList<>();

  @Test
  public void acceptedRegistrationsGenerateResponse() throws Exception {
    IdResponse<Registration> response = new IdResponse<>();

    bus.post(new RegistrationRequest("email@example.com", "password", response));

    assertThat(response.response.id, notNullValue());
    assertThat(response.response.email, equalTo("email@example.com"));
  }

  @Test
  public void acceptedRegistrationsBroadcastEvent() throws Exception {
    bus.register(this);

    bus.post(new RegistrationRequest("email@example.com", "password", new IdResponse<>()));

    assertThat(events, not(empty()));

    assertThat(events.get(0).id, notNullValue());
    assertThat(events.get(0).email, equalTo("email@example.com"));
  }

  @Subscribe
  public void receiveRegistrationNotification(final Registration registration) {
      events.add(registration);
  }
}