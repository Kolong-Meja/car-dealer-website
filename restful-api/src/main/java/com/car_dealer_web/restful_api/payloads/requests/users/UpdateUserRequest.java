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
    String phone_number,
    String address,
    String avatar_url,
    String last_edited_by) {
  public UpdateUserRequest(
      @Size(max = 150) @NotBlank(message = "fullname cannot be blank.") String fullname,

      @Size(max = 2048) @NotBlank(message = "bio cannot be blank.") String bio,

      @Size(max = 150) @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email is not valid.") @NotBlank(message = "email cannot be blank.") String email,

      @Size(max = 16) @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$", message = "phone number is not valid.") @NotBlank(message = "phone number cannot be blank.") String phone_number,

      @Size(min = 20, max = 512) @NotBlank(message = "address cannot be blank.") String address,

      @Pattern(regexp = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$", message = "Avatar url is not valid.") String avatar_url,

      @Size(max = 100) String last_edited_by) {
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = bio;
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.phone_number = Objects.requireNonNull(phone_number, "phone_number cannot be null.");
    this.address = Objects.requireNonNull(address, "address cannot be null.");
    this.avatar_url = avatar_url;
    this.last_edited_by = last_edited_by;
  }
}
