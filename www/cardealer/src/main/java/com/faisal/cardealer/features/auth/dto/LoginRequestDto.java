package com.faisal.cardealer.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
    @NotBlank(message = "Email cannot be blank.") @Size(max = 100, message = "Email must not exceed 100 characters.") @Email(message = "Email format is not valid.") String email,
    @NotBlank(message = "Password cannot be blank.") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.") @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "Password must include at least one uppercase letter, one lowercase letter, and one number.") String password) {
}
