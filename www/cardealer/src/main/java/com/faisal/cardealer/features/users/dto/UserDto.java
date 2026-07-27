package com.faisal.cardealer.features.users.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public record UserDto(String id,
    String username,
    String email,
    String fullName,
    String phoneNumber,
    Set<String> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public UserDto(String id,
      String username,
      String email,
      String fullName,
      String phoneNumber,
      Set<String> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = Objects.requireNonNull(id, "ID cannot be null");
    this.username = Objects.requireNonNull(username, "Username cannot be null");
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
    this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
    this.roles = Objects.requireNonNull(roles, "Roles cannot be null");
    this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
    this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
  }
}
