package com.car_dealer_web.restful_api.repositories;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.car_dealer_web.restful_api.dtos.UserDTO;
import com.car_dealer_web.restful_api.enums.UserAccountStatus;
import com.car_dealer_web.restful_api.enums.UserActiveStatus;
import com.car_dealer_web.restful_api.exceptions.BadRequestException;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.exceptions.UnauthorizedException;
import com.car_dealer_web.restful_api.handlers.JwtAuthHandler;
import com.car_dealer_web.restful_api.interfaces.IAuth;
import com.car_dealer_web.restful_api.interfaces.IUser;
import com.car_dealer_web.restful_api.models.Role;
import com.car_dealer_web.restful_api.models.User;
import com.car_dealer_web.restful_api.payloads.requests.auth.LoginRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RefreshAuthTokenRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RegisterRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Repository
public class AuthRepository implements IAuth {
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  private final EntityManager entityManager;

  private final JwtAuthHandler jwtAuthHandler;

  private final IUser iUser;

  private final static Logger LOG = LoggerFactory.getLogger(AuthRepository.class);

  public AuthRepository(
      EntityManager entityManager,
      JwtAuthHandler jwtAuthHandler,
      IUser iUser) {
    this.entityManager = entityManager;
    this.jwtAuthHandler = jwtAuthHandler;
    this.iUser = iUser;
  }

  @Override
  @Transactional
  public ResponseEntity<ApiResponse<Object>> register(RegisterRequest request) {
    LOG.info("Registering new user...");

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Role> selectQuery = builder.createQuery(Role.class);
    Root<Role> selectRoleRoot = selectQuery.from(Role.class);
    List<Predicate> selectPredicates = new ArrayList<>();

    // WHERE ROLE NOT IN ADMIN AND SUPER ADMIN.
    selectPredicates.add(builder.not(selectRoleRoot.get("name").in("admin", "super admin")));

    // WHERE DELETED_AT IS NULL.
    selectPredicates.add(builder.isNull(selectRoleRoot.get("deleted_at")));

    selectQuery.select(selectRoleRoot)
        .distinct(true)
        .where(builder.and(builder.and(selectPredicates.toArray(Predicate[]::new)),
            builder.equal(selectRoleRoot.get("name"), "user")));

    TypedQuery<Role> typedQuery = entityManager.createQuery(selectQuery);
    var result = typedQuery.getSingleResult();

    Set<Role> defaultRoles = new HashSet<>();
    defaultRoles.add(result);

    User user = new User();
    user.setFullname(request.fullname());
    user.setBio(request.bio());
    user.setPhoneNumber(request.phoneNumber());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setAddress(request.address());
    user.setAccountStatus(UserAccountStatus.ACTIVE.toString().toLowerCase());
    user.setActiveStatus(UserActiveStatus.OFFLINE.toString().toLowerCase());
    user.setAvatarUrl(request.avatarUrl());
    user.setRoles(defaultRoles);

    entityManager.persist(user);
    entityManager.flush();

    // SETUP TOKENS.
    var jwt = jwtAuthHandler.generateToken(user);
    var refreshToken = jwtAuthHandler.generateRefreshToken(user);
    var tokens = new HashMap<String, Object>();
    tokens.put("access_token", jwt);
    tokens.put("refresh_token", refreshToken);

    var resource = new HashMap<String, Object>();
    resource.put("tokens", tokens);
    resource.put("type", "bearer");

    // SETUP THE API (JSON) RESPONSE.
    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true, "Successfully registering new user.",
        now.format(formatter),
        resource);

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> login(LoginRequest request) {
    LOG.info(String.format("Doing login as %s", request.email()));

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    var user = iUser.findOneByEmail(request.email())
        .orElseThrow(() -> new ResourceNotFoundException(String.format("user %s not found.", request.email())));

    var jwt = jwtAuthHandler.generateToken(user);
    var refreshToken = jwtAuthHandler.generateRefreshToken(user);

    var tokens = new HashMap<String, Object>();
    tokens.put("access_token", jwt);
    tokens.put("refresh_token", refreshToken);

    var resource = new HashMap<String, Object>();
    resource.put("tokens", tokens);
    resource.put("type", "bearer");

    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Login is success. Welcome back %s", request.email()),
        now.format(formatter),
        resource);

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> me(HttpServletRequest request) {
    LOG.info("Checking user data by passing JWT.");

    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      LOG.error("Missing or invalid Authorization header.");

      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = iUser.findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("user %s not found", userEmail)));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      LOG.error("Invalid or expired jwt.");

      throw new UnauthorizedException("Invalid or expired jwt.");
    }

    var data = UserDTO.fromObject(user);
    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    var tokenExpiredTime = jwtAuthHandler.extractExpiration(jwt);
    var tokenExpiredAt = Instant.ofEpochMilli(
        tokenExpiredTime.getTime())
        .atZone(ZoneId.of("Asia/Jakarta"))
        .toLocalDateTime()
        .format(formatter);

    var resource = new HashMap<String, Object>();
    resource.put("data", data);
    resource.put("current_time", now.toEpochSecond(ZoneOffset.UTC));
    resource.put("current_datetime", now
        .format(formatter));
    resource.put("token_expired_time", tokenExpiredTime);
    resource.put("token_expired_at", tokenExpiredAt);

    // SETUP THE API (JSON) RESPONSE.
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true, "Successfully fetch personal data.",
        now.format(formatter),
        resource);

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ApiResponse<Object>> refresh(HttpServletRequest httpServletRequest,
      RefreshAuthTokenRequest refreshAuthTokenRequest) {
    LOG.info("Refreshing auth token...");

    final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken = refreshAuthTokenRequest.refreshToken();

    if (header == null || !header.startsWith("Bearer ")) {
      LOG.error("Missing or invalid Authorization header.");

      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = iUser.findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("user %s not found", userEmail)));

    if (!jwtAuthHandler.isTokenValid(refreshToken, user)) {
      LOG.error("Invalid or expired refresh token.");

      throw new UnauthorizedException("Invalid or expired refresh token.");
    }

    var newJwt = jwtAuthHandler.generateToken(user);
    var resource = new HashMap<String, Object>();
    resource.put("access_token", newJwt);
    resource.put("type", "bearer");

    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true, "Successfully refresh auth token.",
        now.format(formatter), resource);

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
