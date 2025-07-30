package com.car_dealer_web.restful_api.dtos.permissions;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Tuple;

public record PermissionJoinDTO(
    String id,
    String name,
    String description,
    String status,
    String last_edited_by,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    LocalDateTime deleted_at,
    String userId,
    String userFullname,
    String userBio,
    String userEmail,
    String userPhoneNumber,
    String userAddress,
    String userAccountStatus,
    String userActiveStatus,
    String userAvatarUrl,
    String roleId,
    String roleName,
    String roleDescription,
    String roleStatus) {
  public PermissionJoinDTO(
      String id,
      String name,
      String description,
      String status,
      String last_edited_by,
      LocalDateTime created_at,
      LocalDateTime updated_at,
      LocalDateTime deleted_at,
      String userId,
      String userFullname,
      String userBio,
      String userEmail,
      String userPhoneNumber,
      String userAddress,
      String userAccountStatus,
      String userActiveStatus,
      String userAvatarUrl,
      String roleId,
      String roleName,
      String roleDescription,
      String roleStatus) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.name = Objects.requireNonNull(name, "name cannot be null.");
    this.description = description;
    this.status = Objects.requireNonNull(status, "status cannot be null.");
    this.last_edited_by = last_edited_by;
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.deleted_at = deleted_at;
    this.userId = Objects.requireNonNull(userId, "user id cannot be null.");
    this.userFullname = Objects.requireNonNull(userFullname, "user fullname cannot be null.");
    this.userBio = userBio;
    this.userEmail = Objects.requireNonNull(userEmail, "user email cannot be null.");
    this.userPhoneNumber = Objects.requireNonNull(userPhoneNumber, "user phone number cannot be null.");
    this.userAddress = userAddress;
    this.userAccountStatus = Objects.requireNonNull(userAccountStatus, "user account status cannot be null.");
    this.userActiveStatus = Objects.requireNonNull(userActiveStatus, "user active status cannot be null.");
    this.userAvatarUrl = userAvatarUrl;
    this.roleId = Objects.requireNonNull(roleId, "role id cannot be null.");
    this.roleName = Objects.requireNonNull(roleName, "role name cannot be null.");
    this.roleDescription = roleDescription;
    this.roleStatus = roleStatus;
  }

  public static final PermissionJoinDTO fromTuple(Tuple tuple) {
    return new PermissionJoinDTO(
        // PERMISSION COLUMNS.
        tuple.get("id", String.class),
        tuple.get("name", String.class),
        tuple.get("description", String.class),
        tuple.get("status", String.class),
        tuple.get("last_edited_by", String.class),
        tuple.get("created_at", LocalDateTime.class),
        tuple.get("updated_at", LocalDateTime.class),
        tuple.get("deleted_at", LocalDateTime.class),

        // USER COLUMNS.
        tuple.get("user_id", String.class),
        tuple.get("user_fullname", String.class),
        tuple.get("user_bio", String.class),
        tuple.get("user_email", String.class),
        tuple.get("user_phone_number", String.class),
        tuple.get("user_address", String.class),
        tuple.get("user_account_status", String.class),
        tuple.get("user_active_status", String.class),
        tuple.get("user_avatar_url", String.class),

        // ROLE COLUMNS.
        tuple.get("role_id", String.class),
        tuple.get("role_name", String.class),
        tuple.get("role_description", String.class),
        tuple.get("role_status", String.class));
  }
}
