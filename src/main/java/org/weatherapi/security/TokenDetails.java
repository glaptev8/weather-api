package org.weatherapi.security;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TokenDetails {
  private Long userId;
  private String token;
  private Date issuedAt;
  private Date expiresAt;
}

