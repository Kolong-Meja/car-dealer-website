package com.car_dealer_web.restful_api.payloads.requests.users;

import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    String fullname,
    String bio,
    String email,
    String phoneNumber,
    String address,
    String avatarUrl,
    String lastEditedBy) {
  public UpdateUserRequest(
      @Size(max = 100) @NotBlank(message = "fullname cannot be blank.") String fullname,

      @Size(max = 250) @NotBlank(message = "bio cannot be blank.") String bio,

      @Size(max = 100) @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email is not valid.") @NotBlank(message = "email cannot be blank.") String email,

      @Size(max = 16) @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$", message = "phone number is not valid.") @NotBlank(message = "phone number cannot be blank.") String phoneNumber,

      @Size(min = 50, max = 250) @NotBlank(message = "address cannot be blank.") String address,

      @Pattern(regexp = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$", message = "Avatar url is not valid.") String avatarUrl,

      @Size(max = 100) String lastEditedBy) {
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = bio;
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber cannot be null.");
    this.address = Objects.requireNonNull(address, "address cannot be null.");
    this.avatarUrl = avatarUrl;
    this.lastEditedBy = lastEditedBy;
  }
}
