package com.faisal.cardealer.features.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto(
    @Size(max = 150, message = "Full name must not exceed 150 characters.") @JsonProperty("full_name") String fullName,

    @Size(max = 20, message = "Phone number must not exceed 20 characters.") @Pattern(regexp = "^(\\+62|62)?[\\s-]?0?8[1-9]\\d[\\s-]?\\d{4}[\\s-]?\\d{2,5}$", message = "Phone number must follow Indonesian format (e.g. +628123456789).") @JsonProperty("phone_number") String phoneNumber)

{
}
