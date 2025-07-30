package com.car_dealer_web.restful_api.dtos.permissions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.car_dealer_web.restful_api.dtos.roles.RoleDTO;
import com.car_dealer_web.restful_api.models.Permission;

public record PermissionWithRolesDTO(
    String id,
    String name,
    String description,
    String status,
    String last_edited_by,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    LocalDateTime deleted_at,
    List<RoleDTO> roles) {
  public PermissionWithRolesDTO(
      String id,
      String name,
      String description,
      String status,
      String last_edited_by,
      LocalDateTime created_at,
      LocalDateTime updated_at,
      LocalDateTime deleted_at,
      List<RoleDTO> roles) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.name = Objects.requireNonNull(name, "name cannot be null.");
    this.description = description;
    this.status = status;
    this.last_edited_by = last_edited_by;
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.deleted_at = deleted_at;
    this.roles = Objects.requireNonNull(roles, "roles cannot be null.");
  }

  public static final PermissionWithRolesDTO fromObject(Permission permission) {
    List<RoleDTO> roles = permission.getRoles()
        .stream()
        .map(role -> new RoleDTO(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getStatus(),
            role.getLastEditedBy(),
            role.getCreatedAt(),
            role.getUpdatedAt(),
            role.getDeletedAt()))
        .toList();

    return new PermissionWithRolesDTO(
        permission.getId(),
        permission.getName(),
        permission.getDescription(),
        permission.getStatus(),
        permission.getLastEditedBy(),
        permission.getCreatedAt(),
        permission.getUpdatedAt(),
        permission.getDeletedAt(),
        roles);
  }
}
