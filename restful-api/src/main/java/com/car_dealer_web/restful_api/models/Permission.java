package com.car_dealer_web.restful_api.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.car_dealer_web.restful_api.annotations.Cuid;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "permission_name_idx", columnList = "name"),
    @Index(name = "permission_status_idx", columnList = "status")
})
public class Permission {
  @Id
  @Cuid
  @GeneratedValue(generator = "cuid")
  @Size(max = 20)
  @NotBlank(message = "id cannot be blank.")
  @Column(name = "id", length = 20, nullable = false)
  private String id;

  @Size(max = 100)
  @NotBlank(message = "name cannot be blank.")
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Size(max = 512)
  @Column(name = "description", columnDefinition = "TEXT", nullable = true)
  private String description;

  @Size(max = 20)
  @ColumnDefault(value = "active")
  @Column(name = "status", length = 20, nullable = true)
  private String status;

  @Size(max = 100)
  @Column(name = "last_edited_by", length = 100, nullable = true)
  private String last_edited_by;

  @CreationTimestamp
  private LocalDateTime created_at;

  @UpdateTimestamp
  private LocalDateTime updated_at;

  @Column(name = "deleted_at")
  private LocalDateTime deleted_at;

  @ManyToMany(fetch = FetchType.LAZY, cascade = {
      CascadeType.PERSIST,
      CascadeType.MERGE
  }, mappedBy = "permissions")
  private Set<Role> roles;

  public Permission() {
  }

  public Permission(
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

  // GETTERS
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getStatus() {
    return status;
  }

  public String getLastEditedBy() {
    return last_edited_by;
  }

  public LocalDateTime getCreatedAt() {
    return created_at;
  }

  public LocalDateTime getUpdatedAt() {
    return updated_at;
  }

  public LocalDateTime getDeletedAt() {
    return deleted_at;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  // SETTERS
  public void setId(String value) {
    this.id = value;
  }

  public void setName(String value) {
    this.name = value;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public void setLastEditedBy(String value) {
    this.last_edited_by = value;
  }

  public void setCreatedAt(LocalDateTime value) {
    this.created_at = value;
  }

  public void setUpdatedAt(LocalDateTime value) {
    this.updated_at = value;
  }

  public void setDeletedAt(LocalDateTime value) {
    this.deleted_at = value;
  }

  public void setRoles(Set<Role> values) {
    this.roles = values;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    Permission permission = (Permission) obj;
    return Objects.equals(id, permission.id)
        && Objects.equals(name, permission.name)
        && Objects.equals(description, permission.description)
        && Objects.equals(status, permission.status)
        && Objects.equals(last_edited_by, permission.last_edited_by)
        && Objects.equals(created_at, permission.created_at)
        && Objects.equals(updated_at, permission.updated_at)
        && Objects.equals(deleted_at, permission.deleted_at);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        name,
        description,
        status,
        last_edited_by,
        created_at,
        updated_at,
        deleted_at);
  }

  @Override
  public String toString() {
    return "Permission{" +
        "id=" + id +
        ", name=" + name +
        ", description=" + description +
        ", status=" + status +
        ", last_edited_by=" + last_edited_by +
        ", created_at=" + created_at +
        ", updated_at=" + updated_at +
        ", deleted_at=" + deleted_at +
        '}';
  }
}
