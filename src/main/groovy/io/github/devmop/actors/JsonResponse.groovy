package io.github.devmop.actors

import com.fasterxml.jackson.databind.ObjectMapper

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD

class JsonResponse<T> implements Response<T> {

  private static final ObjectMapper MAPPER = init()

  private static ObjectMapper init() {
    new ObjectMapper().setVisibility(FIELD, PUBLIC_ONLY)
  }

  def response

  JsonResponse(Response<String> response) {
    this.response = response
  }

  @Override
  void send(final T t) {
    response.send(MAPPER.writeValueAsString(t))
  }
}
