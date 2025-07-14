package com.car_dealer_web.restful_api.dtos;

import java.time.LocalDateTime;
import java.util.Objects;

import com.car_dealer_web.restful_api.models.Role;

import jakarta.persistence.Tuple;

public record RoleDTO(
    String id,
    String name,
    String description,
    String status,
    String last_edited_by,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    LocalDateTime deleted_at) {
  public RoleDTO(
      String id,
      String name,
      String description,
      String status,
      String last_edited_by,
      LocalDateTime created_at,
      LocalDateTime updated_at,
      LocalDateTime deleted_at) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.name = Objects.requireNonNull(name, "name cannot be null.");
    this.description = description;
    this.status = status;
    this.last_edited_by = last_edited_by;
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.deleted_at = deleted_at;
  }

  public static final RoleDTO fromObject(Role role) {
    return new RoleDTO(
        role.getId(),
        role.getName(),
        role.getDescription(),
        role.getStatus(),
        role.getLastEditedBy(),
        role.getCreatedAt(),
        role.getUpdatedAt(),
        role.getDeletedAt());
  }

  public static final RoleDTO fromTuple(Tuple tuple) {
    return new RoleDTO(
        tuple.get("id", String.class),
        tuple.get("name", String.class),
        tuple.get("description", String.class),
        tuple.get("status", String.class),
        tuple.get("last_edited_by", String.class),
        tuple.get("created_at", LocalDateTime.class),
        tuple.get("updated_at", LocalDateTime.class),
        tuple.get("deleted_at", LocalDateTime.class));
  }
}
