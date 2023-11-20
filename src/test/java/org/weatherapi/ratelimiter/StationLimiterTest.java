//package org.weatherapi.ratelimiter;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.weatherapi.config.TestContainerConfig;
//import org.weatherapi.controller.AuthControllerImpl;
//import org.weatherapi.controller.StationControllerImpl;
//import org.weatherapi.entity.User;
//import org.weatherapi.service.StationServiceImpl;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
////@WebFluxTest(StationControllerImpl.class)
//public class StationLimiterTest extends TestContainerConfig {
//  @Autowired
//  private WebTestClient webTestClient;
//
//  @MockBean
//  private StationServiceImpl stationService;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  @Autowired
//  private AuthControllerImpl authController;
//
//  @Test
//  public void getStationsTest() throws JsonProcessingException {
//    when(stationService.getAllStation()).thenReturn(Flux.just());
//
//    User user = new User("test2", "test2");
//
//    webTestClient.post().uri("/api/register")
//      .contentType(MediaType.APPLICATION_JSON)
//      .bodyValue(objectMapper.writeValueAsString(user))
//      .exchange()
//      .expectStatus().isOk();
//
//    String jwtToken = webTestClient.post().uri("/api/login")
//      .contentType(MediaType.APPLICATION_JSON)
//      .bodyValue(user)
//      .exchange()
//      .expectStatus().isOk()
//      .returnResult(String.class)
//      .getResponseBody()
//      .blockFirst();
//
//    String apiKey = webTestClient.post().uri("/api/get-api-key")
//      .header("Authorization", "Bearer " + jwtToken)
//      .exchange()
//      .expectStatus().isOk()
//      .returnResult(String.class)
//      .getResponseBody()
//      .blockFirst();
//
//    for (int i = 0; i < 3; i++) {
//      webTestClient.get().uri("/api/stations")
//        .header("x-api-key", apiKey)
//        .exchange()
//        .expectStatus().isOk();
//    }
//
//    webTestClient.get().uri("/api/stations")
//      .exchange()
//      .expectStatus().is4xxClientError();
//  }
//}
