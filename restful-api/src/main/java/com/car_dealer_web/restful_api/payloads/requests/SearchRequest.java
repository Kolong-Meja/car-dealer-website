package com.car_dealer_web.restful_api.payloads.requests;

import java.util.Objects;

public record SearchRequest(String query) {
  public SearchRequest(String query) {
    this.query = Objects.requireNonNull(query, "query cannot be null.");
  }
}
