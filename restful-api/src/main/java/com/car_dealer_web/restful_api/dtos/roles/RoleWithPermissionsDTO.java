package com.car_dealer_web.restful_api.dtos.roles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.car_dealer_web.restful_api.dtos.permissions.PermissionDTO;
import com.car_dealer_web.restful_api.models.Role;

public record RoleWithPermissionsDTO(
    String id,
    String name,
    String description,
    String status,
    String last_edited_by,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    LocalDateTime deleted_at,
    List<PermissionDTO> permissions) {
  public RoleWithPermissionsDTO(
      String id,
      String name,
      String description,
      String status,
      String last_edited_by,
      LocalDateTime created_at,
      LocalDateTime updated_at,
      LocalDateTime deleted_at,
      List<PermissionDTO> permissions) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.name = Objects.requireNonNull(name, "name cannot be null.");
    this.description = description;
    this.status = status;
    this.last_edited_by = last_edited_by;
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.deleted_at = deleted_at;
    this.permissions = Objects.requireNonNull(permissions, "permissions cannot be null.");
  }

  public static final RoleWithPermissionsDTO fromObject(Role role) {
    List<PermissionDTO> permissions = role.getPermissions()
        .stream()
        .map(permission -> new PermissionDTO(
            permission.getId(),
            permission.getName(),
            permission.getDescription(),
            permission.getStatus(),
            permission.getLastEditedBy(),
            permission.getCreatedAt(),
            permission.getUpdatedAt(),
            permission.getDeletedAt()))
        .toList();

    return new RoleWithPermissionsDTO(
        role.getId(),
        role.getName(),
        role.getDescription(),
        role.getStatus(),
        role.getLastEditedBy(),
        role.getCreatedAt(),
        role.getUpdatedAt(),
        role.getDeletedAt(),
        permissions);
  }
}
