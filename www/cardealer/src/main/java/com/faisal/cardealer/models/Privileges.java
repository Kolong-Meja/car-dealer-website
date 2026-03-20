package com.faisal.cardealer.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Index;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "privileges", indexes = {
    @Index(name = "idx_privileges_name", columnList = "name"),
    @Index(name = "idx_privileges_category", columnList = "category")
})
public class Privileges {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 50, nullable = false, unique = true)
  private String name;

  @Column(name = "category", length = 50, nullable = false)
  private String category;

  @Column(name = "description", length = 255, nullable = false)
  private String description;

  @CreationTimestamp
  private LocalDateTime created_at;

  @UpdateTimestamp
  private LocalDateTime updated_at;

  @ManyToMany(fetch = FetchType.LAZY, cascade = {
      CascadeType.PERSIST,
      CascadeType.MERGE
  }, mappedBy = "privileges")
  private Set<Role> roles;

  public Privileges() {
  }

  public Privileges(Long id, String name, String category, String description, LocalDateTime created_at,
      LocalDateTime updated_at) {
    this.id = Objects.requireNonNull(id, "Privilege ID cannot be null");
    this.name = Objects.requireNonNull(name, "Privilege name cannot be null");
    this.category = Objects.requireNonNull(category, "Privilege category cannot be null");
    this.description = Objects.requireNonNull(description, "Privilege description cannot be null");
    this.created_at = created_at;
    this.updated_at = updated_at;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getCreatedAt() {
    return created_at;
  }

  public void setCreatedAt(LocalDateTime created_at) {
    this.created_at = created_at;
  }

  public LocalDateTime getUpdatedAt() {
    return updated_at;
  }

  public void setUpdatedAt(LocalDateTime updated_at) {
    this.updated_at = updated_at;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

}
