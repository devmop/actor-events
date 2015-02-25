package io.github.devmop.events

import io.github.devmop.actors.Actor

import java.util.concurrent.ConcurrentLinkedQueue


class EventBus {

  private def Collection<Actor> actors = new ConcurrentLinkedQueue<>();

  def void send(message) {
    actors*.receive(message)
  }

  def void register(Actor actor) {
    actors.add(actor)
  }
}
