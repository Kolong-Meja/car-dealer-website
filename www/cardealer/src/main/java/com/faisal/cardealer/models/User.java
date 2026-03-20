package com.faisal.cardealer.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TimeZoneColumn;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.faisal.cardealer.annotations.UUIDv7;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_phone_number", columnList = "phone_number")
})
public class User implements UserDetails {
  @Id
  @UUIDv7
  @GeneratedValue
  @Column(name = "id", length = 36, nullable = false)
  private String id;

  @Column(name = "username", length = 100, nullable = false, unique = true)
  private String username;

  @Column(name = "email", length = 100, nullable = false, unique = true)
  private String email;

  @Column(name = "password", length = 100, nullable = false)
  private String password;

  @Column(name = "fullname", length = 150, nullable = false)
  private String fullname;

  @Column(name = "phone_number", length = 20, nullable = false, unique = true)
  private String phone_number;

  @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
  private boolean is_active;

  @CreationTimestamp
  private LocalDateTime created_at;

  @UpdateTimestamp
  private LocalDateTime updated_at;

  @TimeZoneColumn(name = "deleted_at")
  private LocalDateTime deleted_at;

  @Column(name = "deleted_by", length = 36, nullable = true)
  private String deleted_by;

  @ManyToMany(fetch = FetchType.LAZY, cascade = {
      CascadeType.PERSIST,
      CascadeType.MERGE
  })
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
  private Set<Role> roles;

  public User() {
  }

  public User(String id,
      String username,
      String email,
      String password,
      String fullname,
      String phone_number,
      boolean is_active,
      LocalDateTime created_at,
      LocalDateTime updated_at,
      LocalDateTime deleted_at,
      String deleted_by) {
    this.id = Objects.requireNonNull(id, "User ID cannot be null");
    this.username = Objects.requireNonNull(username, "Username cannot be null");
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.password = Objects.requireNonNull(password, "Password cannot be null");
    this.fullname = Objects.requireNonNull(fullname, "Full name cannot be null");
    this.phone_number = Objects.requireNonNull(phone_number, "Phone number cannot be null");
    this.is_active = is_active;
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.deleted_at = deleted_at;
    this.deleted_by = deleted_by;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserName() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getPhoneNumber() {
    return phone_number;
  }

  public void setPhoneNumber(String phone_number) {
    this.phone_number = phone_number;
  }

  public boolean isIsActive() {
    return is_active;
  }

  public void setIsActive(boolean is_active) {
    this.is_active = is_active;
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

  public LocalDateTime getDeletedAt() {
    return deleted_at;
  }

  public void setDeletedAt(LocalDateTime deleted_at) {
    this.deleted_at = deleted_at;
  }

  public String getDeletedBy() {
    return deleted_by;
  }

  public void setDeletedBy(String deleted_by) {
    this.deleted_by = deleted_by;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
        .collect(Collectors.toSet());
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
