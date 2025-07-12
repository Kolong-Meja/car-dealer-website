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
    String lastEditedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt) {
  public RoleDTO(
      String id,
      String name,
      String description,
      String status,
      String lastEditedBy,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.name = Objects.requireNonNull(name, "name cannot be null.");
    this.description = description;
    this.status = status;
    this.lastEditedBy = lastEditedBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
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
