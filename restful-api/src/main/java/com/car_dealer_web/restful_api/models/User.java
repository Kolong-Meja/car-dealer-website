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
    @Index(name = "fullname_idx", columnList = "fullname"),
    @Index(name = "email_idx", columnList = "email"),
    @Index(name = "phone_number_idx", columnList = "phone_number"),
    @Index(name = "account_status_idx", columnList = "account_status")
})
public class User implements UserDetails {
  @Id
  @Cuid
  @GeneratedValue(generator = "cuid")
  @Size(max = 20)
  @NotBlank(message = "id cannot be blank.")
  @Column(name = "id", length = 20, nullable = false)
  private String id;

  @Size(max = 100)
  @NotBlank(message = "fullname cannot be blank.")
  @Column(name = "fullname", length = 100, nullable = false)
  private String fullname;

  @Size(max = 250)
  @NotBlank(message = "bio cannot be blank.")
  @Column(name = "bio", length = 250, nullable = true)
  private String bio;

  @Size(max = 100)
  @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email is not valid.")
  @NotBlank(message = "email cannot be blank.")
  @Column(name = "email", length = 100, nullable = false, unique = true)
  private String email;

  @Size(min = 8, max = 50)
  @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "password is not valid.")
  @NotBlank(message = "password cannot be blank.")
  @Column(name = "password", length = 50, nullable = false)
  private String password;

  @Size(max = 16)
  @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$", message = "phone number is not valid.")
  @NotBlank(message = "phone number cannot be blank.")
  @Column(name = "phone_number", length = 16, nullable = false, unique = true)
  private String phoneNumber;

  @Size(min = 50, max = 250)
  @NotBlank(message = "address cannot be blank.")
  @Column(name = "address", length = 200, nullable = true)
  private String address;

  @Size(max = 20)
  @NotBlank(message = "account status cannot be blank.")
  @ColumnDefault(value = "active")
  @Column(name = "account_status", length = 20, nullable = true)
  private String accountStatus;

  @Size(max = 20)
  @NotBlank(message = "active status cannot be blank.")
  @ColumnDefault(value = "offline")
  @Column(name = "active_status", length = 20, nullable = true)
  private String activeStatus;

  @Pattern(regexp = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$", message = "Avatar url is not valid.")
  @Column(name = "avatar_url", columnDefinition = "TEXT", nullable = true)
  private String avatarUrl;

  @Column(name = "password_change_at")
  private LocalDateTime passwordChangeAt;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Size(max = 100)
  @Column(name = "last_edited_by", length = 100, nullable = true)
  private String lastEditedBy;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

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
      String phoneNumber,
      String address,
      String accountStatus,
      String activeStatus,
      String avatarUrl,
      LocalDateTime passwordChangeAt,
      LocalDateTime lastLoginAt,
      String lastEditedBy,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    this.id = Objects.requireNonNull(id, "id cannot be null.");
    this.fullname = Objects.requireNonNull(fullname, "fullname cannot be null.");
    this.bio = bio;
    this.email = Objects.requireNonNull(email, "email cannot be null.");
    this.password = Objects.requireNonNull(password, "password cannot be null");
    this.phoneNumber = Objects.requireNonNull(phoneNumber, "phone number cannot be null.");
    this.address = address;
    this.accountStatus = Objects.requireNonNull(accountStatus, "account status cannot be null.");
    this.activeStatus = Objects.requireNonNull(activeStatus, "active status cannot be null.");
    this.avatarUrl = avatarUrl;
    this.passwordChangeAt = passwordChangeAt;
    this.lastLoginAt = lastLoginAt;
    this.lastEditedBy = lastEditedBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
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
    return phoneNumber;
  }

  public String getAddress() {
    return address;
  }

  public String getAccountStatus() {
    return accountStatus;
  }

  public String getActiveStatus() {
    return activeStatus;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public LocalDateTime getPasswordChangeAt() {
    return passwordChangeAt;
  }

  public LocalDateTime getLastLoginAt() {
    return lastLoginAt;
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
    this.phoneNumber = value;
  }

  public void setAddress(String value) {
    this.address = value;
  }

  public void setAccountStatus(String value) {
    this.accountStatus = value;
  }

  public void setActiveStatus(String value) {
    this.activeStatus = value;
  }

  public void setAvatarUrl(String value) {
    this.avatarUrl = value;
  }

  public void setPasswordChangeAt(LocalDateTime value) {
    this.passwordChangeAt = value;
  }

  public void setLastLoginAt(LocalDateTime value) {
    this.lastLoginAt = value;
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
        && Objects.equals(phoneNumber, user.phoneNumber)
        && Objects.equals(address, user.address)
        && Objects.equals(accountStatus, user.accountStatus)
        && Objects.equals(activeStatus, user.activeStatus)
        && Objects.equals(avatarUrl, user.avatarUrl)
        && Objects.equals(passwordChangeAt, user.passwordChangeAt)
        && Objects.equals(lastLoginAt, user.lastLoginAt)
        && Objects.equals(lastEditedBy, user.lastEditedBy)
        && Objects.equals(createdAt, user.createdAt)
        && Objects.equals(updatedAt, user.updatedAt)
        && Objects.equals(deletedAt, user.deletedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        fullname,
        bio,
        email,
        password,
        phoneNumber,
        address,
        accountStatus,
        activeStatus,
        avatarUrl,
        passwordChangeAt,
        lastLoginAt,
        lastEditedBy,
        createdAt,
        updatedAt,
        deletedAt);
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", fullname=" + fullname +
        ", bio=" + bio +
        ", email=" + email +
        ", password=" + password +
        ", phoneNumber=" + phoneNumber +
        ", address=" + address +
        ", accountStatus=" + accountStatus +
        ", activeStatus=" + activeStatus +
        ", avatarUrl=" + avatarUrl +
        ", passwordChangeAt=" + passwordChangeAt +
        ", lastLoginAt=" + lastLoginAt +
        ", lastEditedBy=" + lastEditedBy +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", deletedAt=" + deletedAt +
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
