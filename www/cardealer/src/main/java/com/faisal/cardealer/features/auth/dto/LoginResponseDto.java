package com.faisal.cardealer.features.auth.dto;

import java.util.Map;

public record LoginResponseDto(Map<String, Object> tokens, String type) {
  public LoginResponseDto(Map<String, Object> tokens, String type) {
    this.tokens = Map.copyOf(tokens);
    this.type = type;
  }
}
