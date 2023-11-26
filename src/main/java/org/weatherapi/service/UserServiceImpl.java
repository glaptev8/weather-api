package org.weatherapi.service;

import java.util.Base64;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.weatherapi.dto.UserDto;
import org.weatherapi.entity.User;
import org.weatherapi.exception.AuthException;
import org.weatherapi.mapper.MapStructMapper;
import org.weatherapi.repository.UserRepository;
import org.weatherapi.repository.ApiKeyRepository;
import org.weatherapi.service.api.UserService;
import org.weatherapi.util.ApiKeyRateLimiterUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final ApiKeyRepository apiKeyRepository;
  private final MapStructMapper mapper;
  private final PasswordEncoder passwordEncoder;
  private final ApiKeyRateLimiterUtil apiKeyRateLimiterUtil;

  @Override
  public Mono<UserDto> saveUser(User user) {
    log.info("saving user: {}", user);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository
      .existsByName(user.getName())
      .flatMap(exist -> {
        if (!exist) {
          return userRepository
            .save(user)
            .map(mapper::userToDto);
        }
        log.info("user already exists: {}", user);
        return Mono.error(new AuthException("user already exists", "WEATHERAPI_USER_EXISTS"));
      })
      .doOnSuccess(savedUser -> log.info("user was saved: {}", savedUser));
  }

  @Override
  public Mono<String> generateNewApiKey(Long userId) {
    log.info("generation new apiKey");
    var newApiKey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    return userRepository
      .getApiKeyByUserId(userId)
      .doOnSuccess(oldApiKey -> log.info("old api key: {}", oldApiKey))
      .flatMap(oldApiKey -> apiKeyRepository.removeApiKey(oldApiKey)
        .then(Mono.defer(() -> {
          apiKeyRateLimiterUtil.deleteRateLimiterForOldKey(oldApiKey);
          return Mono.empty();
        }))
        .then(apiKeyRepository.addApiKey(newApiKey)
                .then(userRepository.updateApiKey(newApiKey, userId)
                        .then(Mono.just(newApiKey)))))
      .switchIfEmpty(apiKeyRepository.addApiKey(newApiKey)
          .then(userRepository.updateApiKey(newApiKey, userId)
                  .then(Mono.just(newApiKey))))
      .doOnSuccess(generatedApiKey -> log.info("new api key was updated: {}", generatedApiKey));
  }
}
