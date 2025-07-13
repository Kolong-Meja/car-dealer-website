package com.car_dealer_web.restful_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.car_dealer_web.restful_api")
@EntityScan(basePackages = "com.car_dealer_web.restful_api.models")
@EnableCaching
public class RestfulApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestfulApiApplication.class, args);
  }
}
