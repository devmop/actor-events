package io.github.devmop.application;

import io.github.devmop.events.*;


public interface System {

  public static EventBus initialise(Subsystem... subsystems) {
    EventBus bus = new EventBus();
    for (Subsystem subsystem : subsystems) {
      subsystem.initialise(bus);
    }

    bus.send(Message.create(new Start()));
    return bus;
  }

  public static void shutdown(EventBus bus) {
    bus.send(Message.create(new Terminate()));
  }
}
