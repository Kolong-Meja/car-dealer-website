package com.car_dealer_web.restful_api.payloads.responses;

import java.util.Objects;

public record ApiResponse<T>(
    int status,
    boolean success,
    String message,
    String timestamps,
    T resource) {
  public ApiResponse(
      int status,
      boolean success,
      String message,
      String timestamps,
      T resource) {
    this.status = Objects.requireNonNull(status, "status cannot be null.");
    this.success = Objects.requireNonNull(success, "success cannot be null.");
    this.message = Objects.requireNonNull(message, "message cannot be null.");
    this.timestamps = Objects.requireNonNull(timestamps, "timestamps cannot be null.");
    this.resource = Objects.requireNonNull(resource, "resource cannot be null.");
  }
}
