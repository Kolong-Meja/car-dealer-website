package com.car_dealer_web.restful_api.repositories;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.car_dealer_web.restful_api.dtos.users.UserDTO;
import com.car_dealer_web.restful_api.dtos.users.UserJoinDTO;
import com.car_dealer_web.restful_api.exceptions.AccessDeniedException;
import com.car_dealer_web.restful_api.exceptions.BadRequestException;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.exceptions.UnauthorizedException;
import com.car_dealer_web.restful_api.handlers.JwtAuthHandler;
import com.car_dealer_web.restful_api.interfaces.IUser;
import com.car_dealer_web.restful_api.models.Permission;
import com.car_dealer_web.restful_api.models.Role;
import com.car_dealer_web.restful_api.models.User;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.users.UpdateUserRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Repository
public class UserRepository implements IUser {
  private final EntityManager entityManager;
  private final JwtAuthHandler jwtAuthHandler;
  private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

  public UserRepository(
      EntityManager entityManager,
      JwtAuthHandler jwtAuthHandler) {
    this.entityManager = entityManager;
    this.jwtAuthHandler = jwtAuthHandler;
  }

  private final void searchRequestPredicate(
      SearchRequest request, List<Predicate> predicates, CriteriaBuilder builder, Root<User> root,
      List<String> fields) {
    String queryLower = "%" + request.query().toLowerCase() + "%";

    var likePredicates = fields.stream()
        .map(field -> builder.like(builder.lower(root.get(field)), queryLower))
        .toList();

    if (!likePredicates.isEmpty()) {
      predicates.add(builder.or(likePredicates.toArray(Predicate[]::new)));
    }
  }

  @Override
  @Cacheable(value = "users_cache")
  public ResponseEntity<ApiResponse<PaginationResponse<UserJoinDTO>>> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    LOG.info("Fetching all user entity resources...");

    try {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();

      // ------------------COUNT QUERY-----------------------
      CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
      Root<User> countUserRoot = countQuery.from(User.class);
      Join<User, Role> countJoinWithRole = countUserRoot.join("roles", JoinType.INNER);
      List<Predicate> countPredicates = new ArrayList<>();

      // WHERE USER NOT IN ROLE ADMIN AND SUPER ADMIN.
      countPredicates.add(
          builder.not(countJoinWithRole.get("name").in("admin", "super admin")));

      // WHERE DELETED_AT IS NULL.
      countPredicates.add(builder.isNull(countUserRoot.get("deleted_at")));

      // CASE WHEN SEARCH REQUEST INCLUDED.
      searchRequestPredicate(searchRequest, countPredicates, builder, countUserRoot,
          Arrays.asList("fullname", "email", "phone_number"));

      countQuery.select(builder.countDistinct(countUserRoot))
          .where(builder.and(countPredicates.toArray(Predicate[]::new)));
      Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

      // ------------------SELECT QUERY------------------------
      CriteriaQuery<Tuple> selectQuery = builder.createQuery(Tuple.class);
      Root<User> selectUserRoot = selectQuery.from(User.class);
      Join<User, Role> selectJoinWithRole = selectUserRoot.join("roles", JoinType.INNER);
      Join<Role, Permission> selectJoinWithPermission = selectUserRoot.join("permissions", JoinType.INNER);
      List<Predicate> selectPredicates = new ArrayList<>();

      // WHERE USER NOT IN ROLE ADMIN AND SUPER ADMIN.
      selectPredicates.add(builder
          .not(selectJoinWithRole.get("name").in("admin", "super admin")));

      // WHERE DELETED_AT IS NULL.
      selectPredicates.add(builder.isNull(selectUserRoot.get("deleted_at")));

      // CASE WHEN SEARCH REQUEST INCLUDED.
      searchRequestPredicate(searchRequest, selectPredicates, builder, selectUserRoot,
          Arrays.asList("fullname", "email", "phone_number"));

      selectQuery.multiselect(
          // USERS SELECTED COLUMNS.
          selectUserRoot.get("id").alias("id"),
          selectUserRoot.get("fullname").alias("fullname"),
          selectUserRoot.get("bio").alias("bio"),
          selectUserRoot.get("email").alias("email"),
          selectUserRoot.get("phone_number").alias("phone_number"),
          selectUserRoot.get("address").alias("address"),
          selectUserRoot.get("account_status").alias("account_status"),
          selectUserRoot.get("active_status").alias("active_status"),
          selectUserRoot.get("avatar_url").alias("avatar_url"),
          selectUserRoot.get("last_login_at").alias("last_login_at"),
          selectUserRoot.get("last_edited_by").alias("last_edited_by"),
          selectUserRoot.get("created_at").alias("created_at"),
          selectUserRoot.get("updated_at").alias("updated_at"),
          selectUserRoot.get("deleted_at").alias("deleted_at"),

          // ROLES SELECTED COLUMNS.
          selectJoinWithRole.get("id").alias("role_id"),
          selectJoinWithRole.get("name").alias("role_name"),
          selectJoinWithRole.get("description").alias("role_description"),
          selectJoinWithRole.get("status").alias("role_status"),

          // PERMISSION SELECTED COLUMNS
          selectJoinWithPermission.get("id").alias("permission_id"),
          selectJoinWithPermission.get("name").alias("permission_name"),
          selectJoinWithPermission.get("description").alias("permission_description"),
          selectJoinWithPermission.get("status").alias("permission_status"))
          .distinct(true)
          .where(builder.and(selectPredicates.toArray(Predicate[]::new)));

      // SORT DATA.
      if (!"desc".equalsIgnoreCase(paginationRequest.direction())) {
        selectQuery.orderBy(
            builder.asc(selectUserRoot.get(paginationRequest.sortField())));
      } else {
        selectQuery.orderBy(
            builder.desc(selectUserRoot.get(paginationRequest.sortField())));
      }

      TypedQuery<Tuple> typedQuery = entityManager.createQuery(selectQuery);

      // OFFSET DATA.
      typedQuery.setFirstResult((paginationRequest.page() - 1) * paginationRequest.size());

      // LIMIT DATA.
      typedQuery.setMaxResults(paginationRequest.size());

      // CONVERT INTO DTO.
      List<Tuple> result = typedQuery.getResultList();
      List<UserJoinDTO> userJoinWithOthers = result.stream()
          .map(UserJoinDTO::fromTuple)
          .toList();

      // SETUP PAGINATION RESPONSE.
      var totalPages = (int) Math.ceil((double) totalElements / paginationRequest.size());
      var hasNext = paginationRequest.page() < totalPages;
      PaginationResponse<UserJoinDTO> resource = new PaginationResponse<>(
          userJoinWithOthers,
          totalPages,
          totalElements,
          paginationRequest.size(),
          paginationRequest.page(),
          hasNext);

      ApiResponse<PaginationResponse<UserJoinDTO>> response = new ApiResponse<>(
          HttpStatus.OK.value(),
          true,
          "Succesfully fetch users.",
          DateTime.now(),
          httpServletRequest.getRequestURI(),
          resource);

      LOG.info(response.message());

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  @Cacheable(value = "users_cache", key = "#id")
  public ResponseEntity<ApiResponse<UserJoinDTO>> findOne(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Fetching user entity with ID %s resource...", id));

    try {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Tuple> selectQuery = builder.createQuery(Tuple.class);
      Root<User> selectUserRoot = selectQuery.from(User.class);
      Join<User, Role> selectJoinWithRole = selectUserRoot.join("roles", JoinType.INNER);
      Join<Role, Permission> selectJoinWithPermission = selectUserRoot.join("permissions", JoinType.INNER);
      List<Predicate> selectPredicates = new ArrayList<>();

      // WHERE USER NOT IN ROLE ADMIN AND SUPER ADMIN.
      selectPredicates.add(builder
          .not(selectJoinWithRole.get("name").in("admin", "super admin")));

      // WHERE DELETED_AT IS NULL.
      selectPredicates.add(builder.isNull(selectUserRoot.get("deleted_at")));

      selectQuery.multiselect(
          // USERS SELECTED COLUMNS.
          selectUserRoot.get("id").alias("id"),
          selectUserRoot.get("fullname").alias("fullname"),
          selectUserRoot.get("bio").alias("bio"),
          selectUserRoot.get("email").alias("email"),
          selectUserRoot.get("phone_number").alias("phone_number"),
          selectUserRoot.get("address").alias("address"),
          selectUserRoot.get("account_status").alias("account_status"),
          selectUserRoot.get("active_status").alias("active_status"),
          selectUserRoot.get("avatar_url").alias("avatar_url"),
          selectUserRoot.get("last_login_at").alias("last_login_at"),
          selectUserRoot.get("created_at").alias("created_at"),
          selectUserRoot.get("updated_at").alias("updated_at"),
          selectUserRoot.get("deleted_at").alias("deleted_at"),

          // ROLES SELECTED COLUMNS.
          selectJoinWithRole.get("id").alias("role_id"),
          selectJoinWithRole.get("name").alias("role_name"),
          selectJoinWithRole.get("description").alias("role_description"),
          selectJoinWithRole.get("status").alias("role_status"),

          // PERMISSION SELECTED COLUMNS
          selectJoinWithPermission.get("id").alias("permission_id"),
          selectJoinWithPermission.get("name").alias("permission_name"),
          selectJoinWithPermission.get("description").alias("permission_description"),
          selectJoinWithPermission.get("status").alias("permission_status"))
          .distinct(true)
          .where(builder.and(
              builder.and(selectPredicates.toArray(Predicate[]::new)),
              builder.equal(selectUserRoot.get("id"), id)));

      // CONVERT INTO DTO.
      TypedQuery<Tuple> typedQuery = entityManager.createQuery(selectQuery);
      var resource = UserJoinDTO.fromTuple(typedQuery.getSingleResult());

      ApiResponse<UserJoinDTO> response = new ApiResponse<>(HttpStatus.OK.value(), true,
          String.format("Successfully fetch user with ID %s", id),
          DateTime.now(),
          httpServletRequest.getRequestURI(),
          resource);

      LOG.info(response.message());

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  @Transactional
  @CacheEvict(value = "users_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> update(String id, UpdateUserRequest updateUserRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Updating user entity with ID %s...", id));

    final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found.", id)));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      throw new UnauthorizedException("Invalid or expired jwt.");
    }

    var data = UserDTO.fromObject(user);

    if (!data.id().equals(id)) {
      throw new AccessDeniedException(
          "Access denied. You don't have permission to access this resource.");
    }

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = builder.createCriteriaUpdate(User.class);
    Root<User> userRoot = criteriaUpdate.from(User.class);

    criteriaUpdate.set("fullname", updateUserRequest.fullname());
    criteriaUpdate.set("bio", updateUserRequest.bio());
    criteriaUpdate.set("phone_number", updateUserRequest.phone_number());
    criteriaUpdate.set("email", updateUserRequest.email());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(userRoot.get("id"), id));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("User with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("User with ID %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully update user entity with ID %s.", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> restore(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Restoring user entity with ID %s...", id));

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = builder.createCriteriaUpdate(User.class);
    Root<User> userRoot = criteriaUpdate.from(User.class);

    criteriaUpdate.set("deleted_at", null);
    criteriaUpdate.where(builder.and(
        builder.equal(userRoot.get("id"), id),
        builder.isNotNull(userRoot.get("deleted_at"))));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("user with ID %s not found or is not deleted.", id));

      throw new ResourceNotFoundException(String.format("user with ID %s not found or is not deleted.", id));
    }

    entityManager.flush();
    entityManager.clear();

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully restore user entity with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> delete(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Soft deleting user entity with ID %s...", id));

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = builder.createCriteriaUpdate(User.class);
    Root<User> userRoot = criteriaUpdate.from(User.class);

    criteriaUpdate.set("deleted_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(userRoot.get("id"), id));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("user with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("user with ID %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully soft delete user with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> forceDelete(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Force deleting user entity with ID %s...", id));

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaDelete<User> criteriaDelete = builder.createCriteriaDelete(User.class);
    Root<User> userRoot = criteriaDelete.from(User.class);

    criteriaDelete.where(builder.equal(userRoot.get("id"), id));

    int deletedCount = entityManager.createQuery(criteriaDelete).executeUpdate();
    if (deletedCount != 1) {
      LOG.error(String.format("user with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("user with id %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully force delete user entity with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public Optional<User> findOneByEmail(String email) {
    LOG.info(String.format("Fetching user entity with email %s resource...", email));

    try {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<User> selectQuery = builder.createQuery(User.class);
      Root<User> userRoot = selectQuery.from(User.class);

      selectQuery.select(userRoot)
          .distinct(true)
          .where(builder.equal(userRoot.get("email"), email));

      userRoot.fetch("roles", JoinType.INNER);

      TypedQuery<User> typedQuery = entityManager.createQuery(selectQuery);
      var result = typedQuery.getSingleResult();

      LOG.info(String.format("Successfully fetch user by email %s", email));

      return Optional.of(result);
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }
}
