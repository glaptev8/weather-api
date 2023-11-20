package org.weatherapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.weatherapi.config.TestContainerConfig;
import org.weatherapi.entity.User;
import org.weatherapi.service.StationServiceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@TestPropertySource(properties = "rate-limiter.enabled=false")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidationApiKeyTest extends TestContainerConfig {
  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private StationServiceImpl stationService;

  @Test
  public void validateApiKeyValidTest() throws JsonProcessingException {
    when(stationService.getAllStation()).thenReturn(Flux.just());

    generateApiKey("test", "test");
    webTestClient.get().uri("/api/stations")
      .header("x-api-key", apiKey)
      .exchange()
      .expectStatus().isOk();
  }

  @Test
  public void validateApiKeyInValidTest() {
    when(stationService.getAllStation()).thenReturn(Flux.just());

    webTestClient.get().uri("/api/stations")
      .header("x-api-key", "invalidApiKey")
      .exchange()
      .expectStatus().isUnauthorized();
  }

  private void generateApiKey(String userName, String password) throws JsonProcessingException {
    if (apiKey != null) {
      return;
    }
    if (jwtToken != null) {
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
    jwtToken = webTestClient.post().uri("/api/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(user)
      .exchange()
      .expectStatus().isOk()
      .returnResult(String.class)
      .getResponseBody()
      .blockFirst();
  }
  private void setApiKey() {
    apiKey = webTestClient.post().uri("/api/get-api-key")
      .header("Authorization", "Bearer " + jwtToken)
      .exchange()
      .expectStatus().isOk()
      .returnResult(String.class)
      .getResponseBody()
      .blockFirst();
  }
}
