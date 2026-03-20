package com.faisal.cardealer.features.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(

    @NotBlank(message = "Username cannot be blank.") @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters.") @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._]{2,99}$", message = "Username must start with a letter and contain only letters, numbers, dots, or underscores.") String username,

    @NotBlank(message = "Email cannot be blank.") @Size(max = 100, message = "Email must not exceed 100 characters.") @Email(message = "Email format is not valid.") String email,

    @NotBlank(message = "Password cannot be blank.") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.") @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "Password must include at least one uppercase letter, one lowercase letter, and one number.") String password,

    @NotBlank(message = "Full name cannot be blank.") @Size(max = 150, message = "Full name must not exceed 150 characters.") @JsonProperty("full_name") String fullName,

    @NotBlank(message = "Phone number cannot be blank.") @Size(max = 20, message = "Phone number must not exceed 20 characters.") @Pattern(regexp = "^(\\+62|62)?[\\s-]?0?8[1-9]\\d[\\s-]?\\d{4}[\\s-]?\\d{2,5}$", message = "Phone number must follow Indonesian format (e.g. +628123456789).") @JsonProperty("phone_number") String phoneNumber

) {
}
