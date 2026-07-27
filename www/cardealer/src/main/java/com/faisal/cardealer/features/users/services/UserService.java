package com.faisal.cardealer.features.users.services;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.faisal.cardealer.dto.PaginationPayload;
import com.faisal.cardealer.exceptions.ResourceNotFoundException;
import com.faisal.cardealer.features.users.dto.UpdateUserRequestDto;
import com.faisal.cardealer.features.users.dto.UserDto;
import com.faisal.cardealer.features.users.repositories.UserRepository;
import com.faisal.cardealer.models.Role;
import com.faisal.cardealer.models.User;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public UserDto getUserById(String id) {
    User user = findActiveUserOrThrow(id);
    return convertToUserDto(user);
  }

  @Transactional(readOnly = true)
  public PaginationPayload<UserDto> getAllUsers(int page, int size) {
    Page<User> result = userRepository.findAllActive(PageRequest.of(page, size));

    var data = result.getContent().stream()
        .map(this::convertToUserDto)
        .collect(Collectors.toList());

    return new PaginationPayload<>(
        data,
        result.getTotalPages(),
        result.getTotalElements(),
        result.getSize(),
        result.getNumber(),
        result.hasNext());
  }

  @Transactional
  public UserDto updateUser(String id, UpdateUserRequestDto request) {
    User user = findActiveUserOrThrow(id);

    if (request.fullName() != null && !request.fullName().isBlank()) {
      user.setFullname(request.fullName());
    }
    if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
      user.setPhoneNumber(request.phoneNumber());
    }

    User saved = userRepository.save(user);
    return convertToUserDto(saved);
  }

  @Transactional
  public void deleteUser(String id, String deletedBy) {
    User user = findActiveUserOrThrow(id);
    user.setDeletedAt(LocalDateTime.now());
    user.setDeletedBy(deletedBy);
    userRepository.save(user);
  }

  private User findActiveUserOrThrow(String id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found."));

    if (user.getDeletedAt() != null) {
      throw new ResourceNotFoundException("User not found.");
    }
    return user;
  }

  private UserDto convertToUserDto(User user) {
    return new UserDto(
        user.getId(),
        user.getUserName(),
        user.getEmail(),
        user.getFullname(),
        user.getPhoneNumber(),
        user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}
