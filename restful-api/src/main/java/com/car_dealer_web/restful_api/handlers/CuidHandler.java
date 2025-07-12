package com.car_dealer_web.restful_api.handlers;

import io.github.thibaultmeyer.cuid.CUID;

public class CuidHandler {
  public static CUID generate() {
    return CUID.randomCUID2(12);
  }
}
