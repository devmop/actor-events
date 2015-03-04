package io.github.devmop.events;

import java.util.Random;

public final class Message {

  private static final Random random = new Random();

  public static Message create(Object content) {
    return new Message(random.nextInt(Integer.MAX_VALUE), content);
  }

  private final int id;
  public final Object content;

  private Message(final int id, final Object content) {
    this.id = id;
    this.content = content;
  }

  public Message child(Object content) {
    return new Message(this.id, content);
  }
}
