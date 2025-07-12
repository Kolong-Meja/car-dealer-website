package com.car_dealer_web.restful_api.payloads.requests.auth;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;

public record RefreshAuthTokenRequest(String refreshToken) {
  public RefreshAuthTokenRequest(@NotBlank(message = "refresh token cannot be blank.") String refreshToken) {
    this.refreshToken = Objects.requireNonNull(refreshToken, "refresh token cannot be null.");
  }
}
