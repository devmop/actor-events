package io.github.devmop.users;

import io.github.devmop.actors.Response;

public final class RegistrationRequest {

  public final String email;
  public final String password;
  private final Response<Registration> response;

  public RegistrationRequest(final String email, final String password, Response<Registration> response) {
    this.email = email;
    this.password = password;
    this.response = response;
  }

  public void reply(final Registration registration) {
    response.send(registration);
  }
}
