package com.car_dealer_web.restful_api.payloads.requests.permissions;

import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DetachRolesRequest(List<String> roleIds) {
  public DetachRolesRequest(
      List<@Size(max = 20) @NotBlank(message = "role id cannot be blank.") String> roleIds) {
    this.roleIds = Objects.requireNonNull(roleIds, "role id cannot be null.");
  }
}
