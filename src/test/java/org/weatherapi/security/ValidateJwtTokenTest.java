package org.weatherapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.weatherapi.config.TestContainerConfig;
import org.weatherapi.dto.ApiKeyResponseDto;
import org.weatherapi.dto.LoginResponseDto;
import org.weatherapi.entity.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidateJwtTokenTest extends TestContainerConfig {
  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void validateJwtTokenValidTest() throws JsonProcessingException {
    generateApiKey("test", "test");
    TestContainerConfig.apiKey = webTestClient.post().uri("/api/get-api-key")
      .header("Authorization", "Bearer " + jwtToken)
      .exchange()
      .expectStatus().isOk()
      .returnResult(ApiKeyResponseDto.class)
      .getResponseBody()
      .blockFirst()
      .apiKey();
  }

  @Test
  public void validateJwtTokenInValidTest() {
    webTestClient.post().uri("/api/get-api-key")
      .header("Authorization", "Bearer " + "invalidJwtToken")
      .exchange()
      .expectStatus().isUnauthorized();
  }

  private void generateApiKey(String userName, String password) throws JsonProcessingException {
    if (TestContainerConfig.apiKey != null) {
      return;
    }
    if (TestContainerConfig.jwtToken != null) {
      setApiKey();
      return;
    }
    if (userName.equals(TestContainerConfig.userName)) {
      setJwtToken(new User(userName, password));
      setApiKey();
      return;
    }
    registerNewUser(new User(userName, password));
    setJwtToken(new User(userName, password));
    setApiKey();
    TestContainerConfig.userName = userName;
  }

  private void registerNewUser(User user) throws JsonProcessingException {
    webTestClient.post().uri("/api/register")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(objectMapper.writeValueAsString(user))
      .exchange()
      .expectStatus().isOk();
  }
  private void setJwtToken(User user) {
    TestContainerConfig.jwtToken = webTestClient.post().uri("/api/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(user)
      .exchange()
      .expectStatus().isOk()
      .returnResult(LoginResponseDto.class)
      .getResponseBody()
      .blockFirst()
      .jwtToken();
  }
  private void setApiKey() {
    TestContainerConfig.apiKey = webTestClient.post().uri("/api/get-api-key")
      .header("Authorization", "Bearer " + jwtToken)
      .exchange()
      .expectStatus().isOk()
      .returnResult(ApiKeyResponseDto.class)
      .getResponseBody()
      .blockFirst()
      .apiKey();
  }
}
