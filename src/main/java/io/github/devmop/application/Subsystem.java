package io.github.devmop.application;

import io.github.devmop.events.*;
import io.github.devmop.http.Http;
import io.github.devmop.users.Users;

public enum Subsystem {
  AUTHENTICATION {

    @Override
    void initialise(final EventBus bus) {
      Listener users = new Users(bus);
      bus.register(users);
    }
  },
  HTTP {

    @Override
    void initialise(final EventBus bus) {
      Listener http = new Http(bus);
      bus.register(http);
    }
  };

  abstract void initialise(EventBus bus);
}
