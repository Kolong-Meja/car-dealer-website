package com.car_dealer_web.restful_api.handlers;

import io.github.thibaultmeyer.cuid.CUID;

public class CuidHandler {
  public static String generate() {
    int maxLength = 16;
    final CUID id = CUID.randomCUID2(maxLength);
    return id.toString();
  }
}
