package com.faisal.cardealer.features.auth.dto;

import java.util.Map;

public record RegisterResponseDto(Map<String, Object> tokens, String type) {
  public RegisterResponseDto(Map<String, Object> tokens, String type) {
    this.tokens = Map.copyOf(tokens);
    this.type = type;
  }
}
