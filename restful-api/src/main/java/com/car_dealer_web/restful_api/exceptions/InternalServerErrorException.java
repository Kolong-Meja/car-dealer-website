package com.car_dealer_web.restful_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends Exception {
  public InternalServerErrorException(String message) {
    super(message);
  }
}
