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
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.car_dealer_web.restful_api.dtos.users.UserDTO;
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
  public Object register(RegisterRequest registerRequest) {
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
            builder.equal(selectRoleRoot.get("name"), "customer")));

    TypedQuery<Role> typedQuery = entityManager.createQuery(selectQuery);
    var result = typedQuery.getSingleResult();

    Set<Role> defaultRoles = new HashSet<>();
    defaultRoles.add(result);

    User user = new User();
    user.setFullname(registerRequest.fullname());
    user.setBio(registerRequest.bio());
    user.setPhoneNumber(registerRequest.phone_number());
    user.setEmail(registerRequest.email());
    user.setPassword(passwordEncoder.encode(registerRequest.password()));
    user.setAddress(registerRequest.address());
    user.setAccountStatus(UserAccountStatus.ACTIVE.toString().toLowerCase());
    user.setActiveStatus(UserActiveStatus.OFFLINE.toString().toLowerCase());
    user.setRoles(defaultRoles);

    entityManager.persist(user);
    entityManager.flush();

    // SETUP TOKENS.
    var jwt = jwtAuthHandler.generateToken(user);
    var refreshToken = jwtAuthHandler.generateRefreshToken(user);
    var tokens = new HashMap<String, Object>();
    tokens.put("access_token", jwt);
    tokens.put("refresh_token", refreshToken);

    Map<String, Object> resource = new HashMap<>();
    resource.put("tokens", tokens);
    resource.put("type", "bearer");

    return resource;
  }

  @Override
  public Object login(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
    var user = iUser.findOneByEmail(loginRequest.email())
        .orElseThrow(() -> new ResourceNotFoundException(String.format("user %s not found.", loginRequest.email())));

    var jwt = jwtAuthHandler.generateToken(user);
    var refreshToken = jwtAuthHandler.generateRefreshToken(user);

    var tokens = new HashMap<String, Object>();
    tokens.put("access_token", jwt);
    tokens.put("refresh_token", refreshToken);

    Map<String, Object> resource = new HashMap<>();
    resource.put("tokens", tokens);
    resource.put("type", "bearer");

    return resource;
  }

  @Override
  public Object me(HttpServletRequest request) {
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

    Map<String, Object> resource = new HashMap<>();
    resource.put("data", data);
    resource.put("current_time", now.toEpochSecond(ZoneOffset.UTC));
    resource.put("current_datetime", now
        .format(formatter));
    resource.put("token_expired_time", tokenExpiredTime);
    resource.put("token_expired_at", tokenExpiredAt);

    return resource;
  }

  @Override
  public Object refresh(RefreshAuthTokenRequest refreshAuthTokenRequest,
      HttpServletRequest httpServletRequest) {
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
    Map<String, Object> resource = new HashMap<>();
    resource.put("access_token", newJwt);
    resource.put("type", "bearer");

    return resource;
  }
}
