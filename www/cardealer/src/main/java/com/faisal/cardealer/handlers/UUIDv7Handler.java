package com.faisal.cardealer.handlers;

import java.security.SecureRandom;
import java.util.UUID;

public class UUIDv7Handler {
  public static UUID generate() {
    long timestamp = System.currentTimeMillis();
    var random = new SecureRandom();

    long mostSigBits = (timestamp << 16);
    long leastSigBits = random.nextLong();

    return new UUID(mostSigBits, leastSigBits);
  }
}
