package org.weatherapi.security;

import java.security.Principal;

public record CustomPrincipal(Long id,
                              String name) implements Principal {

  @Override
  public String getName() {
    return name();
  }
}
