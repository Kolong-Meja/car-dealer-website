package com.car_dealer_web.restful_api.handlers;

import java.util.UUID;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class IdGenerator implements IdentifierGenerator {
  @Override
  public Object generate(SharedSessionContractImplementor session, Object object) {
    UUID generatedUuid = UUIDv7Handler.generate();
    return generatedUuid.toString();
  }
}
