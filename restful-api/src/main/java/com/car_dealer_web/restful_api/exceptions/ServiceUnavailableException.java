package com.car_dealer_web.restful_api.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends RuntimeException {
  public ServiceUnavailableException(String message) {
    super(message);
  }

  public ApiResponse<Object> response(final String path) {
    return new ApiResponse<>(HttpStatus.SERVICE_UNAVAILABLE.value(), false, getMessage(), DateTime.now(), path, Map.of());
  }
}
