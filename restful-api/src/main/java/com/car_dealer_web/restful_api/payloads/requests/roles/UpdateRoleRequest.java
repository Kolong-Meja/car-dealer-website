package com.car_dealer_web.restful_api.payloads.requests.roles;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
    String name,
    String description,
    String status,
    String lastEditedBy) {
  public UpdateRoleRequest(
      @Size(max = 50) @NotBlank(message = "name cannot be blank.") String name,
      @NotBlank(message = "description cannot be blank.") String description,
      @Size(max = 20) @NotBlank(message = "status cannot be blank.") String status,
      @Size(max = 100) @NotBlank(message = "last edited by cannot be blank.") String lastEditedBy) {
    this.name = Objects.requireNonNull(name, "name cannot be null.");
    this.description = Objects.requireNonNull(description, "description cannot be null.");
    this.status = Objects.requireNonNull(status, "status cannot be null.");
    this.lastEditedBy = lastEditedBy;
  }
}
