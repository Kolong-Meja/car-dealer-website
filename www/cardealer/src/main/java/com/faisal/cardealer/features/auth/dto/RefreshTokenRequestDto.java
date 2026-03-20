package com.faisal.cardealer.features.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(@NotBlank(message = "Refresh token cannot be blank.") String refreshToken) {
}
