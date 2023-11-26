package org.weatherapi.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class TestContainerConfig {
  private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");
  private static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));

  protected static String apiKey = null;
  protected static String jwtToken = null;
  protected static String userName = null;

  @BeforeAll
  static void setUp() {
    postgresContainer.start();
    System.setProperty("spring.r2dbc.url", String.format("r2dbc:postgresql://%s:%d/%s", postgresContainer.getHost(), postgresContainer.getFirstMappedPort(), postgresContainer.getDatabaseName()));
    System.setProperty("spring.r2dbc.username", postgresContainer.getUsername());
    System.setProperty("spring.r2dbc.password", postgresContainer.getPassword());
    System.setProperty("spring.liquibase.password", postgresContainer.getPassword());
    System.setProperty("spring.liquibase.user", postgresContainer.getUsername());
    System.setProperty("spring.liquibase.url", String.format("jdbc:postgresql://%s:%d/%s", postgresContainer.getHost(), postgresContainer.getFirstMappedPort(), postgresContainer.getDatabaseName()));

    redisContainer.start();
    System.setProperty("spring.data.redis.host", redisContainer.getHost());
    System.setProperty("spring.data.redis.port", String.valueOf(redisContainer.getFirstMappedPort()));
  }

//  @AfterAll
//  static void close(){
//    redisContainer.stop();
//    postgresContainer.stop();
//  }
}
