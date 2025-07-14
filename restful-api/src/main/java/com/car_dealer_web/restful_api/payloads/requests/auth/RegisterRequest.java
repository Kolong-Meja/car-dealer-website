package com.car_dealer_web.restful_api.payloads.requests.auth;

import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    String fullname,
    String bio,
    String email,
    String password,
    String phone_number,
    String address) {
  public RegisterRequest(
      @Size(max = 100) @NotBlank(message = "fullname cannot be blank.") String fullname,

      @Size(max = 250) @NotBlank(message = "bio cannot be blank.") String bio,

      @Size(max = 100) @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email is not valid.") @NotBlank(message = "email cannot be blank.") String email,

      @Size(min = 8, max = 100) @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "password is not valid.") @NotBlank(message = "password cannot be blank.") String password,

      @Size(max = 16) @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$", message = "phone number is not valid.") @NotBlank(message = "phone number cannot be blank.") String phone_number,

      @Size(min = 20, max = 250) @NotBlank(message = "address cannot be blank.") String address) {
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = Objects.requireNonNull(bio, "bio cannot be null.");
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.password = Objects.requireNonNull(password, "password cannot be null.");
    this.phone_number = Objects.requireNonNull(phone_number, "phone number cannot be null.");
    this.address = Objects.requireNonNull(address, "address cannot be null.");
  }
}
