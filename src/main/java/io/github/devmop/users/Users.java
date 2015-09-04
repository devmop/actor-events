package io.github.devmop.users;

import com.google.common.eventbus.*;
import java.util.*;

public class Users {

  private Map<String, Registration> users = new TreeMap<>();
  private long id = 1L;
  private final EventBus bus;

  public Users(EventBus bus) {
    bus.register(this);
    this.bus = bus;
  }

  @Subscribe
  public void register(final RegistrationRequest registrationRequest) {
    Registration registration = new Registration(Long.toString(id++), registrationRequest.email);
    users.put(registration.email, registration);
    registrationRequest.reply(registration);

    bus.post(registration);
  }
}
