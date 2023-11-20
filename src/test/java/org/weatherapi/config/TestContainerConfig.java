package org.weatherapi.config;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

public abstract class TestContainerConfig {
  private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");
  private static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));

  protected static String apiKey;
  protected static String jwtToken;
  protected static String userName;

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
}
