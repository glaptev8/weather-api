package org.weatherapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.filter.OncePerRequestFilter;
import org.weatherapi.security.ApiKeyFilter;
import org.weatherapi.security.AuthenticationManager;
import org.weatherapi.security.BearerTokenServerAuthenticationConverter;
import org.weatherapi.security.JwtHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
  @Value("${jwt.secret}")
  private String secret;
  private final ApiKeyFilter apiKeyFilter;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {
    return http
      .csrf().disable()
      .authorizeExchange()
      .pathMatchers("/**")
      .permitAll()
      .anyExchange()
      .authenticated()
      .and()
      .exceptionHandling()
      .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
      .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
      .and()
      .addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
      .addFilterAt(apiKeyFilter, SecurityWebFiltersOrder.AUTHENTICATION)
      .build();
  }

  private AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authenticationManager) {
    var authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
    authenticationWebFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
    authenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/get-api-key"));

    return authenticationWebFilter;
  }
}
