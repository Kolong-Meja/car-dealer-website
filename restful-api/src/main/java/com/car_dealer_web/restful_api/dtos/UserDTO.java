package com.car_dealer_web.restful_api.dtos;

import java.time.LocalDateTime;
import java.util.Objects;

import com.car_dealer_web.restful_api.models.User;

import jakarta.persistence.Tuple;

public record UserDTO(
    String id,
    String fullname,
    String bio,
    String email,
    String phoneNumber,
    String address,
    String accountStatus,
    String activeStatus,
    String avatarUrl,
    LocalDateTime lastLoginAt,
    String lastEditedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt) {
  public UserDTO(
      String id,
      String fullname,
      String bio,
      String email,
      String phoneNumber,
      String address,
      String accountStatus,
      String activeStatus,
      String avatarUrl,
      LocalDateTime lastLoginAt,
      String lastEditedBy,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = bio;
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.phoneNumber = Objects.requireNonNull(phoneNumber, "phone number cannot be null.");
    this.address = address;
    this.accountStatus = Objects.requireNonNull(accountStatus, "account status cannot be null.");
    this.activeStatus = Objects.requireNonNull(activeStatus, "active status cannot be null.");
    this.avatarUrl = avatarUrl;
    this.lastLoginAt = lastLoginAt;
    this.lastEditedBy = lastEditedBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
  }

  public static final UserDTO fromObject(User user) {
    return new UserDTO(
        user.getId(),
        user.getFullname(),
        user.getBio(),
        user.getEmail(),
        user.getPhoneNumber(),
        user.getAddress(),
        user.getAccountStatus(),
        user.getActiveStatus(),
        user.getAvatarUrl(),
        user.getLastLoginAt(),
        user.getLastEditedBy(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getDeletedAt());
  }

  public static final UserDTO fromTuple(Tuple tuple) {
    return new UserDTO(
        tuple.get("id", String.class),
        tuple.get("fullname", String.class),
        tuple.get("bio", String.class),
        tuple.get("email", String.class),
        tuple.get("phone_number", String.class),
        tuple.get("address", String.class),
        tuple.get("account_status", String.class),
        tuple.get("active_status", String.class),
        tuple.get("avatar_url", String.class),
        tuple.get("last_login_at", LocalDateTime.class),
        tuple.get("last_edited_by", String.class),
        tuple.get("created_at", LocalDateTime.class),
        tuple.get("updated_at", LocalDateTime.class),
        tuple.get("deleted_at", LocalDateTime.class));
  }
}
