package com.car_dealer_web.restful_api.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TimeZoneColumn;
import org.hibernate.annotations.UpdateTimestamp;

import com.car_dealer_web.restful_api.annotations.Cuid;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "roles", indexes = {
    @Index(name = "name_idx", columnList = "name"),
    @Index(name = "status_idx", columnList = "status")
})
public class Role {
  @Id
  @Cuid
  @Size(max = 15)
  @NotBlank(message = "id cannot be blank.")
  @Column(name = "id", length = 15, nullable = false)
  private String id;

  @Size(max = 50)
  @NotBlank(message = "name cannot be blank.")
  @Column(name = "name", length = 50, nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT", nullable = true)
  private String description;

  @Size(max = 20)
  @ColumnDefault(value = "active")
  @Column(name = "status", length = 20, nullable = true)
  private String status;

  @Size(max = 100)
  @Column(name = "last_edited_by", length = 100, nullable = true)
  private String lastEditedBy;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @TimeZoneColumn(name = "deleted_at")
  private LocalDateTime deletedAt;

  @ManyToMany(fetch = FetchType.LAZY, cascade = {
      CascadeType.PERSIST,
      CascadeType.MERGE
  }, mappedBy = "roles")
  private Set<User> users;

  public Role() {
  }

  public Role(
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
    return lastEditedBy;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public Set<User> getUsers() {
    return users;
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
    this.lastEditedBy = value;
  }

  public void setCreatedAt(LocalDateTime value) {
    this.createdAt = value;
  }

  public void setUpdatedAt(LocalDateTime value) {
    this.updatedAt = value;
  }

  public void setDeletedAt(LocalDateTime value) {
    this.deletedAt = value;
  }

  public void setUsers(Set<User> values) {
    this.users = values;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    Role role = (Role) obj;
    return Objects.equals(id, role.id)
        && Objects.equals(name, role.name)
        && Objects.equals(description, role.description)
        && Objects.equals(status, role.status)
        && Objects.equals(lastEditedBy, role.lastEditedBy)
        && Objects.equals(createdAt, role.createdAt)
        && Objects.equals(updatedAt, role.updatedAt)
        && Objects.equals(deletedAt, role.deletedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        name,
        description,
        status,
        lastEditedBy,
        createdAt,
        updatedAt,
        deletedAt);
  }

  @Override
  public String toString() {
    return "Role{" +
        "id=" + id +
        ", name=" + name +
        ", description=" + description +
        ", status=" + status +
        ", lastEditedBy=" + lastEditedBy +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", deletedAt=" + deletedAt +
        '}';
  }
}
