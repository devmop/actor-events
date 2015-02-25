package io.github.devmop.users

import groovy.transform.CompileStatic
import io.github.devmop.actors.Response

@CompileStatic
class RegistrationRequest {

  public final String email
  public final String password
  private final Response<Registration> response

  RegistrationRequest(final String email, final String password, Response<Registration> response) {
    this.email = email
    this.password = password
    this.response = response
  }

  void reply(final Registration registration) {
    response.send(registration)
  }
}
