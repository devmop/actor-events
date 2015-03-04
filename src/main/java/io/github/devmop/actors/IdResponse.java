package io.github.devmop.actors;


public class IdResponse<T> implements Response<T> {

  public T response;

  @Override
  public void send(final T t) {
    response = t;
  }
}
