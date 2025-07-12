package com.car_dealer_web.restful_api.payloads.requests.auth;

import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    String email,
    String password) {
  public LoginRequest(
      @Size(max = 100) @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email is not valid.") @NotBlank(message = "email cannot be blank.") String email,

      @Size(min = 8, max = 50) @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "password is not valid.") @NotBlank(message = "password cannot be blank.") String password) {
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.password = Objects.requireNonNull(password, "password cannot be null.");
  }
}
