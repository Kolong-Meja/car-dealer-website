package com.car_dealer_web.restful_api.payloads.requests.roles;

import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DetachPermissionsRequest(List<String> permissionIds) {
  public DetachPermissionsRequest(
      List<@Size(max = 20) @NotBlank(message = "permission id cannot be blank.") String> permissionIds) {
    this.permissionIds = Objects.requireNonNull(permissionIds, "permission id cannot be null.");
  }
}
