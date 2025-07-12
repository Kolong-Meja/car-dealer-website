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
    String phoneNumber,
    String address,
    String accountStatus,
    String activeStatus,
    String avatarUrl) {
  public RegisterRequest(
      @Size(max = 100) @NotBlank(message = "fullname cannot be blank.") String fullname,

      @Size(max = 250) @NotBlank(message = "bio cannot be blank.") String bio,

      @Size(max = 100) @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email is not valid.") @NotBlank(message = "email cannot be blank.") String email,

      @Size(min = 8, max = 50) @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "password is not valid.") @NotBlank(message = "password cannot be blank.") String password,

      @Size(max = 16) @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$", message = "phone number is not valid.") @NotBlank(message = "phone number cannot be blank.") String phoneNumber,

      @Size(min = 50, max = 250) @NotBlank(message = "address cannot be blank.") String address,

      @Size(max = 20) @NotBlank(message = "account status cannot be blank.") String accountStatus,

      @Size(max = 20) @NotBlank(message = "active status cannot be blank.") String activeStatus,

      @Pattern(regexp = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$", message = "Avatar url is not valid.") String avatarUrl) {
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = Objects.requireNonNull(bio, "bio cannot be null.");
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.password = Objects.requireNonNull(password, "password cannot be null.");
    this.phoneNumber = Objects.requireNonNull(phoneNumber, "phone number cannot be null.");
    this.address = Objects.requireNonNull(address, "address cannot be null.");
    this.accountStatus = Objects.requireNonNull(accountStatus, "account status cannot be null.");
    this.activeStatus = Objects.requireNonNull(activeStatus, "active status cannot be null.");
    this.avatarUrl = Objects.requireNonNull(avatarUrl, "avatar url cannot be null.");
  }
}
