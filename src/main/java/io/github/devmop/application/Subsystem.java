package io.github.devmop.application;

import com.google.common.eventbus.EventBus;
import io.github.devmop.http.Http;
import io.github.devmop.users.Users;

public enum Subsystem {
  AUTHENTICATION {

    @Override
    void initialise(final EventBus bus) {
      new Users(bus);
    }
  },
  HTTP {

    @Override
    void initialise(final EventBus bus) {
      new Http(bus);
    }
  };

  abstract void initialise(EventBus bus);
}
