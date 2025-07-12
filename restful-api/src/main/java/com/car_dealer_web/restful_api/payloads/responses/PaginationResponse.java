package com.car_dealer_web.restful_api.payloads.responses;

import java.util.Collection;
import java.util.Objects;

public record PaginationResponse<T>(
    Collection<T> data,
    int totalPages,
    long totalElements,
    int size,
    int page,
    boolean hasNext) {
  public PaginationResponse(
      Collection<T> data,
      int totalPages,
      long totalElements,
      int size,
      int page,
      boolean hasNext) {
    this.data = Objects.requireNonNull(data, "data cannot be null.");
    this.totalPages = Objects.requireNonNull(totalPages, "totalPages cannot be null.");
    this.totalElements = Objects.requireNonNull(totalElements, "totalElements cannot be null.");
    this.size = Objects.requireNonNull(size, "size cannot be null.");
    this.page = Objects.requireNonNull(page, "page cannot be null.");
    this.hasNext = Objects.requireNonNull(hasNext, "hasNext cannot be null.");
  }
}
