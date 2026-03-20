package com.faisal.cardealer.features.auth.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public record RegisterResponseDto(
    String id,
    String username,
    String email,
    String fullName,
    String phoneNumber,
    Set<String> roles,
    LocalDateTime createdAt) {
  public RegisterResponseDto(
      String id,
      String username,
      String email,
      String fullName,
      String phoneNumber,
      Set<String> roles, LocalDateTime createdAt) {
    this.id = Objects.requireNonNull(id, "ID cannot be null");
    this.username = Objects.requireNonNull(username, "Username cannot be null");
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
    this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
    this.roles = Objects.requireNonNull(roles, "Roles cannot be null");
    this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
  }
}
