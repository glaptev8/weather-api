package org.weatherapi.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("weather_user")
public class User {
  @Id
  private Long id;
  private String name;
  private String password;
  private String apiKey;

  public User(String name, String password) {
    this.name = name;
    this.password = password;
  }
}
