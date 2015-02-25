package io.github.devmop.application

import io.github.devmop.events.EventBus

import java.awt.Event


class System {

  def static EventBus initialise(Subsystem... subsystems) {
    def bus = new EventBus()
    subsystems*.initialise(bus)

    bus.send(new Start())
    return bus;
  }

  def static shutdown(EventBus bus) {
    bus.send(new Terminate())
  }
}
