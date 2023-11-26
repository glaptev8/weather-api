package org.weatherapi.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.weatherapi.entity.Station;
import org.weatherapi.entity.Weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {

  @Bean
  public ObjectMapper objectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Bean
  @Qualifier("apiKeyTemplate")
  public ReactiveRedisTemplate<String, String> apiKeyReactiveRedisTemplate(
    ReactiveRedisConnectionFactory connectionFactory) {
    return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContext.string());
  }

  @Bean
  @Qualifier("stationTemplate")
  public ReactiveRedisTemplate<String, Station> stationReactiveRedisTemplate(ObjectMapper objectMapper, ReactiveRedisConnectionFactory factory) {
    RedisSerializationContext<String, Station> serializationContext = RedisSerializationContext
      .<String, Station>newSerializationContext()
      .key(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
      .value(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Station.class)))
      .hashKey(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
      .hashValue(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Station.class)))
      .build();

    return new ReactiveRedisTemplate<>(factory, serializationContext);
  }

  @Bean
  @Qualifier("weatherTemplate")
  public ReactiveRedisTemplate<String, Weather> weatherReactiveRedisTemplate(
    ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {

    RedisSerializationContext<String, Weather> serializationContext = RedisSerializationContext
      .<String, Weather>newSerializationContext()
      .key(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
      .value(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Weather.class)))
      .hashKey(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
      .hashValue(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Weather.class)))
      .build();

    return new ReactiveRedisTemplate<>(factory, serializationContext);
  }
}
