package com.faisal.cardealer.dto;

import java.util.Objects;

public record ResponsePayload(
    int status,
    boolean success,
    String message,
    String timestamps,
    Object resource) {
  public ResponsePayload(int status, boolean success, String message, String timestamps, Object resource) {
    this.status = Objects.requireNonNull(status);
    this.success = success;
    this.message = Objects.requireNonNull(message);
    this.timestamps = Objects.requireNonNull(timestamps);
    this.resource = Objects.requireNonNull(resource);
  }
}
