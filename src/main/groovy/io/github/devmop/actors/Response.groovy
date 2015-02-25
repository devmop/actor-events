package io.github.devmop.actors


interface Response<T> {

  void send(T t);
}