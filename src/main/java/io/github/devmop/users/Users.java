package io.github.devmop.users;

import java.util.*;

import io.github.devmop.events.EventBus;
import io.github.devmop.events.Listener;
import io.github.devmop.events.Message;

public class Users implements Listener {

  private Map<String, Registration> users = new TreeMap<>();
  private long id = 1L;
  private final EventBus bus;

  public Users(EventBus bus) {
    this.bus = bus;
  }

  @Override
  public void receive(final Message message) {
    if (message.content instanceof RegistrationRequest) {
      acceptRegistration(message, (RegistrationRequest) message.content);
    }
  }

  private void acceptRegistration(final Message message, final RegistrationRequest registrationRequest) {
    Registration registration = new Registration(Long.toString(id++), registrationRequest.email);
    users.put(registration.email, registration);
    registrationRequest.reply(registration);

    bus.send(message.child(registration));
  }
}
