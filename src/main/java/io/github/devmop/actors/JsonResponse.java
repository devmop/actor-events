package io.github.devmop.actors;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

public class JsonResponse<T> implements Response<T> {

  private static final ObjectMapper MAPPER = init();

  private static ObjectMapper init() {
    return new ObjectMapper().setVisibility(FIELD, PUBLIC_ONLY);
  }

  private final Response<String> response;

  public JsonResponse(Response<String> response) {
    this.response = response;
  }

  @Override
  public void send(final T t) {
    try {
      response.send(MAPPER.writeValueAsString(t));
    }
    catch (IOException e) {
      //Meh
    }
  }
}
