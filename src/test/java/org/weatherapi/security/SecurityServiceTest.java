package org.weatherapi.security;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.weatherapi.config.TestContainerConfig;
import org.weatherapi.entity.User;
import org.weatherapi.exception.AuthException;
import org.weatherapi.repository.UserRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class SecurityServiceTest extends TestContainerConfig {

  @Autowired
  private SecurityService securityService;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Test
  public void authenticationWhenPasswordCorrectTest() {
    var user = new User(1L, "testUser", "123", null);

    when(userRepository.findByName(user.getName())).thenReturn(Mono.just(user));
    when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);

    StepVerifier.create(securityService.authentication(user.getName(), user.getPassword()))
      .expectNextMatches(tokenDetails ->
                           tokenDetails.getUserId().equals(user.getId()) &&
                           StringUtils.isNotBlank(tokenDetails.getToken()) &&
                           Objects.nonNull(tokenDetails.getIssuedAt()) &&
                           Objects.nonNull(tokenDetails.getExpiresAt()))
      .verifyComplete();
  }

  @Test
  public void authenticationWhenPasswordInCorrectTest() {
    var user = new User(1L, "testUser", "123", null);

    when(userRepository.findByName(user.getName())).thenReturn(Mono.just(user));
    when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(false);

    StepVerifier.create(securityService.authentication(user.getName(), "incorrectPassword"))
      .expectErrorMatches(throwable -> {
        if (throwable instanceof AuthException ex) {
          return ex.getErrorCode().equals("WEATHERAPI_INVALID_PASSWORD");
        }
        return false;
      })
      .verify();
  }

  @Test
  public void authenticationWhenUserNotFoundTest() {
    var user = new User(1L, "notExistUser", "123", null);
    when(userRepository.findByName(user.getName())).thenReturn(Mono.empty());

    StepVerifier.create(securityService.authentication(user.getName(), "1234"))
      .expectErrorMatches(throwable -> {
        if (throwable instanceof AuthException ex) {
          return ex.getErrorCode().equals("WEATHERAPI_USER_NOT_FOUND");
        }
        return false;
      })
      .verify();
  }
}