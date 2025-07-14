package com.car_dealer_web.restful_api.dtos.joins;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Tuple;

public record UserJoinDTO(
    String id,
    String fullname,
    String bio,
    String email,
    String phone_number,
    String address,
    String account_status,
    String active_status,
    String avatar_url,
    LocalDateTime last_login_at,
    String last_edited_by,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    LocalDateTime deleted_at,
    String roleId,
    String roleName,
    String roleDescription,
    String roleStatus) {
  public UserJoinDTO(
      String id,
      String fullname,
      String bio,
      String email,
      String phone_number,
      String address,
      String account_status,
      String active_status,
      String avatar_url,
      LocalDateTime last_login_at,
      String last_edited_by,
      LocalDateTime created_at,
      LocalDateTime updated_at,
      LocalDateTime deleted_at,
      String roleId,
      String roleName,
      String roleDescription,
      String roleStatus) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = bio;
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.phone_number = Objects.requireNonNull(phone_number, "phone_number cannot be null.");
    this.address = address;
    this.account_status = Objects.requireNonNull(account_status, "account status cannot be null");
    this.active_status = Objects.requireNonNull(active_status, "active status cannot be null");
    this.avatar_url = avatar_url;
    this.last_login_at = last_login_at;
    this.last_edited_by = last_edited_by;
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.deleted_at = deleted_at;
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
