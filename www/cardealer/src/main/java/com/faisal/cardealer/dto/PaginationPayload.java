package com.faisal.cardealer.dto;

import java.util.Collection;
import java.util.Objects;

public record PaginationPayload<T>(
    Collection<T> data,
    int totalPages,
    long totalElements,
    int size,
    int page,
    boolean hasNext) {
  public PaginationPayload(Collection<T> data, int totalPages, long totalElements, int size, int page,
      boolean hasNext) {
    this.data = Objects.requireNonNull(data);
    this.totalPages = Objects.requireNonNull(totalPages);
    this.totalElements = Objects.requireNonNull(totalElements);
    this.size = Objects.requireNonNull(size);
    this.page = Objects.requireNonNull(page);
    this.hasNext = Objects.requireNonNull(hasNext);
  }
}
