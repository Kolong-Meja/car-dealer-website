package com.car_dealer_web.restful_api.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends Exception {
  public InternalServerErrorException(String message) {
    super(message);
  }

  public ApiResponse<Object> response(final String path) {
    return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, getMessage(), DateTime.now(), path, Map.of());
  }
}
