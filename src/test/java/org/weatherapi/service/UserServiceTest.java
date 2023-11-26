package org.weatherapi.service;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.weatherapi.dto.UserDto;
import org.weatherapi.entity.User;
import org.weatherapi.mapper.MapStructMapper;
import org.weatherapi.repository.ApiKeyRepository;
import org.weatherapi.repository.UserRepository;
import org.weatherapi.util.ApiKeyRateLimiterUtil;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private ApiKeyRepository apiKeyRepository;
  @Mock
  private MapStructMapper mapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock
  private ApiKeyRateLimiterUtil apiKeyRateLimiterUtil;
  @InjectMocks
  private UserServiceImpl userService;

  @Test
  public void saveUserTest() {
    final var passwordForUser = "test1";
    final var testUserName = "test1";
    final var testUser = new User(testUserName, passwordForUser);
    final var savedTestUserDto = new UserDto(testUserName, null);
    var encodedPasswordForTestUser = bCryptPasswordEncoder.encode(passwordForUser);
    var savedUserDto = new User(1L, testUserName, encodedPasswordForTestUser, null);

    when(passwordEncoder.encode(passwordForUser)).thenReturn(encodedPasswordForTestUser);
    when(userRepository.save(testUser)).thenReturn(Mono.just(savedUserDto));
    when(userRepository.existsByName(testUser.getName())).thenReturn(Mono.just(false));
    when(mapper.userToDto(savedUserDto)).thenReturn(savedTestUserDto);

    var saveResult = userService.saveUser(testUser);

    StepVerifier.create(saveResult)
      .expectNextMatches(savedUser -> savedUser.equals(savedTestUserDto))
      .verifyComplete();

    verify(passwordEncoder).encode(passwordForUser);
    verify(userRepository).save(testUser);
    verify(mapper).userToDto(savedUserDto);
  }

  @Test
  public void generateNewApiKeyTest() {
    final var userId = 1L;
    final var oldApiKey = UUID.randomUUID().toString();

    when(userRepository.getApiKeyByUserId(userId)).thenReturn(Mono.just(oldApiKey));
    when(apiKeyRepository.removeApiKey(oldApiKey)).thenReturn(Mono.just(true));
    when(apiKeyRepository.addApiKey(any())).thenReturn(Mono.empty());
    when(userRepository.updateApiKey(any(), eq(userId))).thenReturn(Mono.empty());
    doNothing().when(apiKeyRateLimiterUtil).deleteRateLimiterForOldKey(any());
    var saveResult = userService.generateNewApiKey(userId);

    StepVerifier.create(saveResult)
      .expectNextMatches(StringUtils::isNotBlank)
      .verifyComplete();

    verify(userRepository).getApiKeyByUserId(userId);
    verify(apiKeyRepository).removeApiKey(oldApiKey);
  }
}
