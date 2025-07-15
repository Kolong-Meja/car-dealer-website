package com.car_dealer_web.restful_api.payloads.requests.permissions;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
    String name,
    String description) {
  public CreatePermissionRequest(
      @Size(max = 100) @NotBlank(message = "name cannot be blank.") String name,
      @NotBlank(message = "description cannot be blank.") String description) {
    this.name = Objects.requireNonNull(name, "name cannot be null.");
    this.description = Objects.requireNonNull(description, "description cannot be null.");
  }
}
