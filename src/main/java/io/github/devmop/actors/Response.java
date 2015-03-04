package io.github.devmop.actors;


public interface Response<T> {

  public void send(T t);
}