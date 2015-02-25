package io.github.devmop.users

import io.github.devmop.actors.Actor
import io.github.devmop.events.EventBus


class Users implements Actor {

  private def users = [:]
  private long id = 1L
  private def final bus

  Users(EventBus bus) {
    this.bus = bus
  }

  @Override
  void receive(final Object message) {
    if (message instanceof RegistrationRequest) {
      acceptRegistration(message)
    }
  }

  void acceptRegistration(final RegistrationRequest registrationRequest) {
    def registration = new Registration((id++).toString(), registrationRequest.email)
    users[registration.id] = registration
    registrationRequest.reply(registration)

    bus.send(registration)
  }
}
