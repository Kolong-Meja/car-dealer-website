package com.faisal.cardealer.features.auth.dto;

public record RefreshTokenResponseDto(String accessToken, String type) {
  public RefreshTokenResponseDto(String accessToken, String type) {
    this.accessToken = accessToken;
    this.type = type;
  }
}
