package com.car_dealer_web.restful_api.payloads.requests.permissions;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePermissionRequest(
    String name,
    String description,
    String status,
    String last_edited_by) {
  public UpdatePermissionRequest(
      @Size(max = 50) @NotBlank(message = "name cannot be blank.") String name,
      @Size(max = 512) @NotBlank(message = "description cannot be blank.") String description,
      @Size(max = 20) String status,
      @Size(max = 100) String last_edited_by) {
    this.name = Objects.requireNonNull(name, "name cannot be null.");
    this.description = Objects.requireNonNull(description, "description cannot be null.");
    this.status = status;
    this.last_edited_by = last_edited_by;
  }
}
