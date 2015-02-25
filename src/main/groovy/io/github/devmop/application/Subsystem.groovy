package io.github.devmop.application

import groovy.transform.CompileStatic
import io.github.devmop.events.EventBus
import io.github.devmop.http.Http
import io.github.devmop.users.Users

@CompileStatic
enum Subsystem {
  AUTHENTICATION {

    @Override
    void initialise(final EventBus bus) {
      def users = new Users(bus)
      bus.register(users)
    }
  },
  HTTP {

    @Override
    void initialise(final EventBus bus) {
      def http = new Http(bus)
      bus.register(http)
    }
  };

  abstract void initialise(EventBus bus);
}
