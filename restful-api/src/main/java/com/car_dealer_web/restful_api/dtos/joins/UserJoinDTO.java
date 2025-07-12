package com.car_dealer_web.restful_api.dtos.joins;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Tuple;

public record UserJoinDTO(
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
    LocalDateTime deletedAt,
    String roleId,
    String roleName,
    String roleDescription,
    String roleStatus) {
  public UserJoinDTO(
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
      LocalDateTime deletedAt,
      String roleId,
      String roleName,
      String roleDescription,
      String roleStatus) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = bio;
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber cannot be null.");
    this.address = address;
    this.accountStatus = Objects.requireNonNull(accountStatus, "account status cannot be null");
    this.activeStatus = Objects.requireNonNull(activeStatus, "active status cannot be null");
    this.avatarUrl = avatarUrl;
    this.lastLoginAt = lastLoginAt;
    this.lastEditedBy = lastEditedBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
    this.roleId = Objects.requireNonNull(roleId, "role id cannot be null.");
    this.roleName = Objects.requireNonNull(roleName, "role name cannot be null.");
    this.roleDescription = roleDescription;
    this.roleStatus = Objects.requireNonNull(roleStatus, "role status cannot be null.");
  }

  public static final UserJoinDTO fromTuple(Tuple tuple) {
    return new UserJoinDTO(
        // USER COLUMNS.
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
        tuple.get("deleted_at", LocalDateTime.class),

        // ROLE COLUMNS.
        tuple.get("role_id", String.class),
        tuple.get("role_name", String.class),
        tuple.get("role_description", String.class),
        tuple.get("role_status", String.class));
  }
}
