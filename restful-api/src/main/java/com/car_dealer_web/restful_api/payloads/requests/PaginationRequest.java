package com.car_dealer_web.restful_api.payloads.requests;

public record PaginationRequest(
    int page, int size, String direction, String sortField) {
  public PaginationRequest() {
    this(1, 10, "desc", "id");
  }

  public PaginationRequest(
      int page, int size, String direction, String sortField) {
    this.page = page;
    this.size = size;
    this.direction = direction;
    this.sortField = sortField;
  }
}
