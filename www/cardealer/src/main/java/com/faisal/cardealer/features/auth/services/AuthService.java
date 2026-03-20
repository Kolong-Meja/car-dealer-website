package com.faisal.cardealer.features.auth.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.faisal.cardealer.features.auth.dto.RegisterRequestDto;
import com.faisal.cardealer.features.auth.dto.RegisterResponseDto;
import com.faisal.cardealer.features.roles.repositories.RoleRepository;
import com.faisal.cardealer.features.users.repositories.UserRepository;
import com.faisal.cardealer.models.Role;
import com.faisal.cardealer.models.User;

@Service
public class AuthService {

  private static final String DEFAULT_ROLE = "CUSTOMER";
  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public RegisterResponseDto register(RegisterRequestDto request) {
    if (userRepository.existsActiveByUsername(request.username())) {
      log.error("User registration failed: Username '{}' is already taken.", request.username());
      throw new IllegalArgumentException("Username '" + request.username() + "' is already taken.");
    }

    if (userRepository.existsActiveByEmail(request.email())) {
      log.error("User registration failed: Email '{}' is already registered.", request.email());
      throw new IllegalArgumentException("Email '" + request.email() + "' is already registered.");
    }

    Role customerRole = roleRepository.findByName(DEFAULT_ROLE)
        .orElseThrow(() -> new IllegalStateException(
            "Default role '" + DEFAULT_ROLE + "' not found. Run database seed first."));

    User user = new User();
    user.setUsername(request.username());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setFullname(request.fullName());
    user.setPhoneNumber(request.phoneNumber());
    user.setIsActive(true);
    user.setRoles(Set.of(customerRole));

    User savedUser = userRepository.save(user);

    log.info("User registered successfully: {}", savedUser.getEmail());

    return new RegisterResponseDto(
        savedUser.getId(),
        savedUser.getUsername(),
        savedUser.getEmail(),
        savedUser.getFullname(),
        savedUser.getPhoneNumber(),
        savedUser.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet()),
        savedUser.getCreatedAt());
  }
}
