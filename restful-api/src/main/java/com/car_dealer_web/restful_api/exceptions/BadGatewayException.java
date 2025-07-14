package com.car_dealer_web.restful_api.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class BadGatewayException extends RuntimeException {
  public BadGatewayException(String message) {
    super(message);
  }

  public ApiResponse<Object> response(final String path) {
    return new ApiResponse<>(HttpStatus.BAD_GATEWAY.value(), false, getMessage(), DateTime.now(), path, Map.of());
  }
}
