package io.github.devmop.events;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;


public class EventBus {

  private final Collection<Listener> listeners = new ConcurrentLinkedQueue<>();

  public void send(Message message) {
    for (Listener listener : listeners) {
      listener.receive(message);
    }
  }

  public void register(Listener actor) {
    listeners.add(actor);
  }
}
