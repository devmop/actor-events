package io.github.devmop.application;

import com.google.common.eventbus.EventBus;

public interface System {

  public static EventBus initialise(Subsystem... subsystems) {
    EventBus bus = new EventBus();
    for (Subsystem subsystem : subsystems) {
      subsystem.initialise(bus);
    }

    bus.post(new Start());
    return bus;
  }

  public static void shutdown(EventBus bus) {
    bus.post(new Terminate());
  }
}
