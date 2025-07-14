package com.car_dealer_web.restful_api.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends RuntimeException {
  public TooManyRequestsException(String message) {
    super(message);
  }

  public ApiResponse<Object> responseMessage(final String path) {
    return new ApiResponse<>(HttpStatus.TOO_MANY_REQUESTS.value(), false, getMessage(), DateTime.now(), path, Map.of());
  }
}
