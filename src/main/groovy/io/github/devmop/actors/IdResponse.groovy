package io.github.devmop.actors


class IdResponse<T> implements Response<T> {

  def T response

  @Override
  void send(final T t) {
    response = t;
  }
}
