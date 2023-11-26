package org.weatherapi.ratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.weatherapi.config.TestContainerConfig;
import org.weatherapi.dto.ApiKeyResponseDto;
import org.weatherapi.dto.LoginResponseDto;
import org.weatherapi.entity.User;
import org.weatherapi.service.StationServiceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationLimiterTest extends TestContainerConfig {

  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private StationServiceImpl stationService;
  @Value("${ratelimiter.station_limiter.limit-for-period}")
  private int limitForPeriod;

  @Test
  public void getStationsTest() throws JsonProcessingException {
    when(stationService.getAllStation()).thenReturn(Flux.just());
    generateApiKey("test", "test");
    for (int i = 0; i < limitForPeriod; i++) {
      webTestClient.get().uri("/api/stations")
        .header("x-api-key", apiKey)
        .exchange()
        .expectStatus().isOk();
    }

    webTestClient.get().uri("/api/stations")
      .header("x-api-key", apiKey)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

    setApiKey();
    webTestClient.get().uri("/api/stations")
      .header("x-api-key", apiKey)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.OK);
    generateApiKey("test", "test");
  }

  @Test
  public void getStationTest() throws JsonProcessingException {
    when(stationService.getInfoByStation(any())).thenReturn(Mono.empty());

    generateApiKey("test", "test");
    for (int i = 0; i < limitForPeriod; i++) {
      webTestClient.get().uri("/api/stations/testName")
        .header("x-api-key", apiKey)
        .exchange()
        .expectStatus().isOk();
    }

    webTestClient.get().uri("/api/stations/testName")
      .header("x-api-key", apiKey)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    generateApiKey("test", "test");

    setApiKey();
    webTestClient.get().uri("/api/stations/testName")
      .header("x-api-key", apiKey)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.OK);
    generateApiKey("test", "test");
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
