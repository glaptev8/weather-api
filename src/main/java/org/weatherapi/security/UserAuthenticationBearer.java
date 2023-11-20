package org.weatherapi.security;



import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.weatherapi.dto.VerificationResult;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

public class UserAuthenticationBearer {
  public static Mono<Authentication> create(VerificationResult verificationResult) {
    var claims = verificationResult.claims();
    var subject = claims.getSubject();
    var name = claims.get("name", String.class);

    var principalId = Long.parseLong(subject);
    var principal = new CustomPrincipal(principalId, name);

    return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, null));
  }
}
