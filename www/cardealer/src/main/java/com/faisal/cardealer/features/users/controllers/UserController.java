package com.faisal.cardealer.features.users.controllers;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.faisal.cardealer.dto.PaginationPayload;
import com.faisal.cardealer.dto.ResponsePayload;
import com.faisal.cardealer.features.users.dto.UpdateUserRequestDto;
import com.faisal.cardealer.features.users.dto.UserDto;
import com.faisal.cardealer.features.users.services.UserService;
import com.faisal.cardealer.models.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<ResponsePayload> getUserById(@PathVariable String id) {
    UserDto user = userService.getUserById(id);
    return ResponseEntity.ok(new ResponsePayload(
        HttpStatus.OK.value(), true, "User retrieved successfully.", LocalDateTime.now().toString(), user));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<ResponsePayload> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PaginationPayload<UserDto> users = userService.getAllUsers(page, size);
    return ResponseEntity.ok(new ResponsePayload(
        HttpStatus.OK.value(), true, "Users retrieved successfully.", LocalDateTime.now().toString(), users));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<ResponsePayload> updateUser(
      @PathVariable String id,
      @Valid @RequestBody UpdateUserRequestDto request) {
    UserDto updated = userService.updateUser(id, request);
    return ResponseEntity.ok(new ResponsePayload(HttpStatus.OK.value(), true, "User updated successfully.",
        LocalDateTime.now().toString(), updated));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<ResponsePayload> deleteUser(
      @PathVariable String id,
      @AuthenticationPrincipal User currentUser) {
    userService.deleteUser(id, currentUser.getId());
    return ResponseEntity.ok(new ResponsePayload(
        HttpStatus.OK.value(), true, "User deleted successfully.", LocalDateTime.now().toString(), Map.of("id", id)));
  }

  @PutMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ResponsePayload> updateOwnProfile(
      @AuthenticationPrincipal User currentUser,
      @Valid @RequestBody UpdateUserRequestDto request) {
    UserDto updated = userService.updateUser(currentUser.getId(), request);
    return ResponseEntity.ok(new ResponsePayload(
        HttpStatus.OK.value(), true, "Profile updated successfully.",
        LocalDateTime.now().toString(), updated));
  }
}
