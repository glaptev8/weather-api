package org.weatherapi.security;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.weatherapi.entity.User;
import org.weatherapi.exception.AuthException;
import org.weatherapi.repository.UserRepository;
import org.weatherapi.security.TokenDetails.TokenDetailsBuilder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.expiration}")
  private Integer expirationInSeconds;
  @Value("${jwt.issuer}")
  private String issuer;

  private TokenDetailsBuilder generateToken(User user) {
    var expirationDate = new Date(new Date().getTime() + expirationInSeconds * 1000);
    var createdDate = new Date();
    String token = Jwts.builder()
      .setIssuer(issuer)
      .setClaims(new HashMap<>() {{
        put("name", user.getName());
      }})
      .setSubject(user.getId().toString())
      .setIssuedAt(createdDate)
      .setId(UUID.randomUUID().toString())
      .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
      .setExpiration(expirationDate)
      .compact();
    return TokenDetails.builder()
      .token(token)
      .issuedAt(createdDate)
      .expiresAt(expirationDate);
  }

  public Mono<TokenDetails> authentication(String name, String password) {
      return userRepository
        .findByName(name)
        .flatMap(user -> {
          if (!passwordEncoder.matches(password, user.getPassword())) {
            return Mono.error(new AuthException("invalid password", "WEATHERAPI_INVALID_PASSWORD"));
          }
          return Mono.just(generateToken(user)
                             .userId(user.getId())
                             .build());
        })
        .switchIfEmpty(Mono.error(new AuthException("user not found", "WEATHERAPI_USER_NOT_FOUND")));
  }
}
