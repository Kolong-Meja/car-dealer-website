package com.faisal.cardealer.features.auth.services;

import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.faisal.cardealer.exceptions.ResourceNotFoundException;
import com.faisal.cardealer.exceptions.UnauthorizedException;
import com.faisal.cardealer.features.auth.dto.LoginRequestDto;
import com.faisal.cardealer.features.auth.dto.LoginResponseDto;
import com.faisal.cardealer.features.auth.dto.RefreshTokenResponseDto;
import com.faisal.cardealer.features.auth.dto.RegisterRequestDto;
import com.faisal.cardealer.features.auth.dto.RegisterResponseDto;
import com.faisal.cardealer.features.roles.repositories.RoleRepository;
import com.faisal.cardealer.features.users.dto.UserDto;
import com.faisal.cardealer.features.users.repositories.UserRepository;
import com.faisal.cardealer.handlers.JwtAuthHandler;
import com.faisal.cardealer.models.Role;
import com.faisal.cardealer.models.User;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {

  private static final String DEFAULT_ROLE = "USER";
  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtAuthHandler jwtAuthHandler;

  public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
      JwtAuthHandler jwtAuthHandler) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtAuthHandler = jwtAuthHandler;
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

    /**
     * SETUP TOKENS
     */
    var jwt = jwtAuthHandler.generateToken(user);
    var refreshToken = jwtAuthHandler.generateRefreshToken(user);
    var tokens = new HashMap<String, Object>();
    tokens.put("access_token", jwt);
    tokens.put("refresh_token", refreshToken);

    return new RegisterResponseDto(tokens, "bearer");
  }

  public LoginResponseDto login(LoginRequestDto request) {
    User user = userRepository.findActiveByEmail(request.email())
        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      log.error("User login failed: Invalid password for email '{}'.", request.email());
      throw new IllegalArgumentException("Invalid email or password.");
    }

    log.info("User logged in successfully: {}", user.getEmail());

    /**
     * SETUP TOKENS
     */
    var jwt = jwtAuthHandler.generateToken(user);
    var refreshToken = jwtAuthHandler.generateRefreshToken(user);
    var tokens = new HashMap<String, Object>();
    tokens.put("access_token", jwt);
    tokens.put("refresh_token", refreshToken);

    return new LoginResponseDto(tokens, "bearer");
  }

  public UserDto me(HttpServletRequest request) {
    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      log.error("Missing or invalid Authorization header.");
      throw new com.faisal.cardealer.exceptions.BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = userRepository.findActiveByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist."));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      log.error("Invalid or expired JWT for user '{}'.", userEmail);
      throw new UnauthorizedException("Invalid or expired jwt.");
    }

    log.info("User '{}' accessed their profile information.", userEmail);

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getFullname(),
        user.getPhoneNumber(),
        user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  public RefreshTokenResponseDto refresh(HttpServletRequest request) {
    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      log.error("Missing or invalid Authorization header for token refresh.");
      throw new com.faisal.cardealer.exceptions.BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = userRepository.findActiveByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist."));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      log.error("Invalid or expired refresh token for user '{}'.", userEmail);
      throw new UnauthorizedException("Invalid or expired refresh token.");
    }

    var newAccessToken = jwtAuthHandler.generateToken(user);

    log.info("Access token refreshed successfully for user '{}'.", userEmail);

    return new RefreshTokenResponseDto(newAccessToken, "bearer");
  }
}
