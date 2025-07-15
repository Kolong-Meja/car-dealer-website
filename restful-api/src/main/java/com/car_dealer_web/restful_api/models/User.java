package com.car_dealer_web.restful_api.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.car_dealer_web.restful_api.annotations.Cuid;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "user_fullname_idx", columnList = "fullname"),
    @Index(name = "user_email_idx", columnList = "email"),
    @Index(name = "user_phone_number_idx", columnList = "phone_number"),
    @Index(name = "user_account_status_idx", columnList = "account_status")
})
public class User implements UserDetails {
  @Id
  @Cuid
  @GeneratedValue(generator = "cuid")
  @Size(max = 20)
  @NotBlank(message = "id cannot be blank.")
  @Column(name = "id", length = 20, nullable = false)
  private String id;

  @Size(max = 150)
  @NotBlank(message = "fullname cannot be blank.")
  @Column(name = "fullname", length = 150, nullable = false)
  private String fullname;

  @Size(max = 2048)
  @Column(name = "bio", columnDefinition = "TEXT", nullable = true)
  private String bio;

  @Size(max = 150)
  @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email is not valid.")
  @NotBlank(message = "email cannot be blank.")
  @Column(name = "email", length = 150, nullable = false, unique = true)
  private String email;

  @Size(min = 8, max = 100)
  @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "password is not valid.")
  @NotBlank(message = "password cannot be blank.")
  @Column(name = "password", length = 100, nullable = false)
  private String password;

  @Size(max = 16)
  @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$", message = "phone number is not valid.")
  @NotBlank(message = "phone number cannot be blank.")
  @Column(name = "phone_number", length = 16, nullable = false, unique = true)
  private String phone_number;

  @Size(min = 20, max = 512)
  @NotBlank(message = "address cannot be blank.")
  @Column(name = "address", columnDefinition = "TEXT", nullable = true)
  private String address;

  @Size(max = 20)
  @NotBlank(message = "account status cannot be blank.")
  @ColumnDefault(value = "active")
  @Column(name = "account_status", length = 20, nullable = true)
  private String account_status;

  @Size(max = 20)
  @NotBlank(message = "active status cannot be blank.")
  @ColumnDefault(value = "offline")
  @Column(name = "active_status", length = 20, nullable = true)
  private String active_status;

  @Pattern(regexp = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$", message = "Avatar url is not valid.")
  @Column(name = "avatar_url", columnDefinition = "TEXT", nullable = true)
  private String avatar_url;

  @Column(name = "password_change_at")
  private LocalDateTime password_change_at;

  @Column(name = "last_login_at")
  private LocalDateTime last_login_at;

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
  })
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
  private Set<Role> roles;

  public User() {
  }

  public User(
      String id,
      String fullname,
      String bio,
      String email,
      String password,
      String phone_number,
      String address,
      String account_status,
      String active_status,
      String avatar_url,
      LocalDateTime password_change_at,
      LocalDateTime last_login_at,
      String last_edited_by,
      LocalDateTime created_at,
      LocalDateTime updated_at,
      LocalDateTime deleted_at) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = bio;
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.password = Objects.requireNonNull(password, "password cannot be null");
    this.phone_number = Objects.requireNonNull(phone_number, "phone number cannot be null.");
    this.address = address;
    this.account_status = Objects.requireNonNull(account_status, "account status cannot be null.");
    this.active_status = Objects.requireNonNull(active_status, "active status cannot be null.");
    this.avatar_url = avatar_url;
    this.password_change_at = password_change_at;
    this.last_login_at = last_login_at;
    this.last_edited_by = last_edited_by;
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.deleted_at = deleted_at;
  }

  // GETTERS
  public String getId() {
    return id;
  }

  public String getFullname() {
    return fullname;
  }

  public String getBio() {
    return bio;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getPhoneNumber() {
    return phone_number;
  }

  public String getAddress() {
    return address;
  }

  public String getAccountStatus() {
    return account_status;
  }

  public String getActiveStatus() {
    return active_status;
  }

  public String getAvatarUrl() {
    return avatar_url;
  }

  public LocalDateTime getPasswordChangeAt() {
    return password_change_at;
  }

  public LocalDateTime getLastLoginAt() {
    return last_login_at;
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

  public void setFullname(String value) {
    this.fullname = value;
  }

  public void setBio(String value) {
    this.bio = value;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public void setPassword(String value) {
    this.password = value;
  }

  public void setPhoneNumber(String value) {
    this.phone_number = value;
  }

  public void setAddress(String value) {
    this.address = value;
  }

  public void setAccountStatus(String value) {
    this.account_status = value;
  }

  public void setActiveStatus(String value) {
    this.active_status = value;
  }

  public void setAvatarUrl(String value) {
    this.avatar_url = value;
  }

  public void setPasswordChangeAt(LocalDateTime value) {
    this.password_change_at = value;
  }

  public void setLastLoginAt(LocalDateTime value) {
    this.last_login_at = value;
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
  public boolean equals(Object object) {
    if (this == object)
      return true;
    if (object == null || getClass() != object.getClass())
      return false;

    User user = (User) object;
    return Objects.equals(id, user.id)
        && Objects.equals(fullname, user.fullname)
        && Objects.equals(bio, user.bio)
        && Objects.equals(email, user.email)
        && Objects.equals(password, user.password)
        && Objects.equals(phone_number, user.phone_number)
        && Objects.equals(address, user.address)
        && Objects.equals(account_status, user.account_status)
        && Objects.equals(active_status, user.active_status)
        && Objects.equals(avatar_url, user.avatar_url)
        && Objects.equals(password_change_at, user.password_change_at)
        && Objects.equals(last_login_at, user.last_login_at)
        && Objects.equals(last_edited_by, user.last_edited_by)
        && Objects.equals(created_at, user.created_at)
        && Objects.equals(updated_at, user.updated_at)
        && Objects.equals(deleted_at, user.deleted_at);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        fullname,
        bio,
        email,
        password,
        phone_number,
        address,
        account_status,
        active_status,
        avatar_url,
        password_change_at,
        last_login_at,
        last_edited_by,
        created_at,
        updated_at,
        deleted_at);
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", fullname=" + fullname +
        ", bio=" + bio +
        ", email=" + email +
        ", password=" + password +
        ", phone_number=" + phone_number +
        ", address=" + address +
        ", account_status=" + account_status +
        ", active_status=" + active_status +
        ", avatar_url=" + avatar_url +
        ", password_change_at=" + password_change_at +
        ", last_login_at=" + last_login_at +
        ", last_edited_by=" + last_edited_by +
        ", created_at=" + created_at +
        ", updated_at=" + updated_at +
        ", deleted_at=" + deleted_at +
        '}';
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
