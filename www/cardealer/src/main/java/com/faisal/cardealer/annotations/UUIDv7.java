package com.faisal.cardealer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

import com.faisal.cardealer.handlers.IdGenerator;

@IdGeneratorType(IdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UUIDv7 {
  boolean generated() default true;
}
