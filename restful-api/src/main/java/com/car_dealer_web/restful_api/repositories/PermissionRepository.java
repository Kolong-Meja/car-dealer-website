package com.car_dealer_web.restful_api.repositories;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.car_dealer_web.restful_api.dtos.UserDTO;
import com.car_dealer_web.restful_api.dtos.joins.PermissionJoinDTO;
import com.car_dealer_web.restful_api.enums.RoleStatus;
import com.car_dealer_web.restful_api.exceptions.BadRequestException;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.exceptions.UnauthorizedException;
import com.car_dealer_web.restful_api.handlers.JwtAuthHandler;
import com.car_dealer_web.restful_api.interfaces.IPermission;
import com.car_dealer_web.restful_api.interfaces.IUser;
import com.car_dealer_web.restful_api.models.Permission;
import com.car_dealer_web.restful_api.models.Role;
import com.car_dealer_web.restful_api.models.User;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.CreatePermissionRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.UpdatePermissionRequest;
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
public class PermissionRepository implements IPermission {
  private final IUser iUser;
  private final EntityManager entityManager;
  private final JwtAuthHandler jwtAuthHandler;
  private final static Logger LOG = LoggerFactory.getLogger(RoleRepository.class);

  public PermissionRepository(IUser iUser, EntityManager entityManager, JwtAuthHandler jwtAuthHandler) {
    this.iUser = iUser;
    this.entityManager = entityManager;
    this.jwtAuthHandler = jwtAuthHandler;
  }

  private final void searchRequestPredicate(
      SearchRequest request, List<Predicate> predicates, CriteriaBuilder builder, Root<Permission> root,
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
  @Cacheable(value = "permissions_cache")
  public ResponseEntity<ApiResponse<PaginationResponse<PermissionJoinDTO>>> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    LOG.info("Fetching all permission entity resources...");

    try {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();

      // ------------------COUNT QUERY-----------------------
      CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
      Root<Permission> countRoleRoot = countQuery.from(Permission.class);
      List<Predicate> countPredicates = new ArrayList<>();

      // WHERE DELETED_AT IS NULL.
      countPredicates.add(builder.isNull(countRoleRoot.get("deleted_at")));

      // CASE WHEN SEARCH REQUEST INCLUDED.
      searchRequestPredicate(
          searchRequest,
          countPredicates,
          builder,
          countRoleRoot,
          Arrays.asList("name"));

      countQuery.select(builder.countDistinct(countRoleRoot))
          .where(builder.and(countPredicates.toArray(Predicate[]::new)));
      Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

      // ------------------SELECT QUERY------------------------
      CriteriaQuery<Tuple> selectQuery = builder.createQuery(Tuple.class);
      Root<Permission> selectPermissionRoot = selectQuery.from(Permission.class);
      Join<Permission, Role> selectJoinWithRole = selectPermissionRoot.join("roles", JoinType.INNER);
      Join<Role, User> selectJoinWithUser = selectPermissionRoot.join("users", JoinType.INNER);
      List<Predicate> selectPredicates = new ArrayList<>();

      // WHERE DELETED_AT IS NULL.
      selectPredicates.add(builder.isNull(selectPermissionRoot.get("deleted_at")));

      // CASE WHEN SEARCH REQUEST INCLUDED.
      searchRequestPredicate(
          searchRequest,
          countPredicates,
          builder,
          selectPermissionRoot,
          Arrays.asList("name"));

      selectQuery.multiselect(
          // PERMISSIONS SELECTED COLUMNS.
          selectPermissionRoot.get("id").alias("id"),
          selectPermissionRoot.get("name").alias("name"),
          selectPermissionRoot.get("description").alias("description"),
          selectPermissionRoot.get("status").alias("status"),
          selectPermissionRoot.get("last_edited_by").alias("last_edited_by"),
          selectPermissionRoot.get("created_at").alias("created_at"),
          selectPermissionRoot.get("updated_at").alias("updated_at"),
          selectPermissionRoot.get("deleted_at").alias("deleted_at"),

          // USERS SELECTED COLUMNS.
          selectJoinWithUser.get("id").alias("user_id"),
          selectJoinWithUser.get("fullname").alias("user_fullname"),
          selectJoinWithUser.get("bio").alias("user_bio"),
          selectJoinWithUser.get("email").alias("user_email"),
          selectJoinWithUser.get("phone_number").alias("user_phone_number"),
          selectJoinWithUser.get("address").alias("user_address"),
          selectJoinWithUser.get("account_status").alias("user_account_status"),
          selectJoinWithUser.get("active_status").alias("user_active_status"),
          selectJoinWithUser.get("avatar_url").alias("user_avatar_url"),
          selectJoinWithUser.get("last_login_at").alias("user_last_login_at"),

          // ROLE SELECTED COLUMNS.
          selectJoinWithRole.get("id").alias("permission_id"),
          selectJoinWithRole.get("name").alias("permission_name"),
          selectJoinWithRole.get("description").alias("permission_description"),
          selectJoinWithRole.get("status").alias("permission_status"))
          .distinct(true)
          .where(builder.and(selectPredicates.toArray(Predicate[]::new)));

      // SORT DATA.
      if (!"desc".equalsIgnoreCase(paginationRequest.direction())) {
        selectQuery.orderBy(
            builder.asc(selectPermissionRoot.get(paginationRequest.sortField())));
      } else {
        selectQuery.orderBy(
            builder.desc(selectPermissionRoot.get(paginationRequest.sortField())));
      }

      TypedQuery<Tuple> typedQuery = entityManager.createQuery(selectQuery);

      // OFFSET DATA.
      typedQuery.setFirstResult((paginationRequest.page() - 1) * paginationRequest.size());

      // LIMIT DATA.
      typedQuery.setMaxResults(paginationRequest.size());

      // CONVERT INTO DTO.
      List<Tuple> result = typedQuery.getResultList();
      List<PermissionJoinDTO> permissionJoinWithOthers = result.stream()
          .map(PermissionJoinDTO::fromTuple)
          .toList();

      // SETUP PAGINATION RESPONSE.
      var totalPages = (int) Math.ceil((double) totalElements / paginationRequest.size());
      var hasNext = paginationRequest.page() < totalPages;
      PaginationResponse<PermissionJoinDTO> resource = new PaginationResponse<>(
          permissionJoinWithOthers,
          totalPages,
          totalElements,
          paginationRequest.size(),
          paginationRequest.page(),
          hasNext);

      ApiResponse<PaginationResponse<PermissionJoinDTO>> response = new ApiResponse<>(
          HttpStatus.OK.value(),
          true,
          "Succesfully fetch permissions.",
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
  @Cacheable(value = "permissions_cache", key = "#id")
  public ResponseEntity<ApiResponse<PermissionJoinDTO>> findOne(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Fetching permission entity with ID %s resource...", id));

    try {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Tuple> selectQuery = builder.createQuery(Tuple.class);
      Root<Permission> selectPermissionRoot = selectQuery.from(Permission.class);
      Join<Permission, Role> selectJoinWithRole = selectPermissionRoot.join("roles", JoinType.INNER);
      Join<Role, User> selectJoinWithUser = selectPermissionRoot.join("users", JoinType.INNER);
      List<Predicate> selectPredicates = new ArrayList<>();

      // WHERE DELETED_AT IS NULL.
      selectPredicates.add(builder.isNull(selectPermissionRoot.get("deleted_at")));

      selectQuery.multiselect(
          // PERMISSIONS SELECTED COLUMNS.
          selectPermissionRoot.get("id").alias("id"),
          selectPermissionRoot.get("name").alias("name"),
          selectPermissionRoot.get("description").alias("description"),
          selectPermissionRoot.get("status").alias("status"),
          selectPermissionRoot.get("last_edited_by").alias("last_edited_by"),
          selectPermissionRoot.get("created_at").alias("created_at"),
          selectPermissionRoot.get("updated_at").alias("updated_at"),
          selectPermissionRoot.get("deleted_at").alias("deleted_at"),

          // USERS SELECTED COLUMNS.
          selectJoinWithUser.get("id").alias("user_id"),
          selectJoinWithUser.get("fullname").alias("user_fullname"),
          selectJoinWithUser.get("bio").alias("user_bio"),
          selectJoinWithUser.get("email").alias("user_email"),
          selectJoinWithUser.get("phone_number").alias("user_phone_number"),
          selectJoinWithUser.get("address").alias("user_address"),
          selectJoinWithUser.get("account_status").alias("user_account_status"),
          selectJoinWithUser.get("active_status").alias("user_active_status"),
          selectJoinWithUser.get("avatar_url").alias("user_avatar_url"),
          selectJoinWithUser.get("last_login_at").alias("user_last_login_at"),

          // ROLE SELECTED COLUMNS.
          selectJoinWithRole.get("id").alias("permission_id"),
          selectJoinWithRole.get("name").alias("permission_name"),
          selectJoinWithRole.get("description").alias("permission_description"),
          selectJoinWithRole.get("status").alias("permission_status"))
          .distinct(true)
          .where(builder.and(
              builder.and(selectPredicates.toArray(Predicate[]::new)),
              builder.equal(selectPermissionRoot.get("id"), id)));

      // CONVERT INTO DTO.
      TypedQuery<Tuple> typedQuery = entityManager.createQuery(selectQuery);
      PermissionJoinDTO resource = PermissionJoinDTO.fromTuple(typedQuery.getSingleResult());

      ApiResponse<PermissionJoinDTO> response = new ApiResponse<>(HttpStatus.OK.value(), true,
          String.format("Successfully fetch permission with ID %s", id), DateTime.now(),
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
  @CachePut(value = "permissions_cache", key = "#result.resource.id")
  public ResponseEntity<ApiResponse<Permission>> save(
      CreatePermissionRequest createPermissionRequest, HttpServletRequest httpServletRequest) {
    LOG.info("Creating new role...");

    Permission permission = new Permission();
    permission.setName(createPermissionRequest.name());
    permission.setDescription(createPermissionRequest.description());
    permission.setStatus(RoleStatus.ACTIVE.toString().toLowerCase());

    entityManager.persist(permission);
    entityManager.flush();

    ApiResponse<Permission> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        "Successfully add new permission.",
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        permission);

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  @Transactional
  @CacheEvict(value = "permissions_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> update(String id, UpdatePermissionRequest updatePermissionRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Updating permission entity with ID %s...", id));

    final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = iUser.findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found.", id)));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      throw new UnauthorizedException("Invalid or expired jwt.");
    }

    var data = UserDTO.fromObject(user);

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Permission> criteriaUpdate = builder.createCriteriaUpdate(Permission.class);
    Root<Permission> permissionRoot = criteriaUpdate.from(Permission.class);

    criteriaUpdate.set("name", updatePermissionRequest.name());
    criteriaUpdate.set("description", updatePermissionRequest.description());
    criteriaUpdate.set("status", updatePermissionRequest.status());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(permissionRoot.get("id"), id));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("permission with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("permission with ID %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully update permission entity with ID %s.", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "permissions_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> restore(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Restoring permission entity with ID %s...", id));

    final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = iUser.findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found.", id)));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      throw new UnauthorizedException("Invalid or expired jwt.");
    }

    var data = UserDTO.fromObject(user);

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Permission> criteriaUpdate = builder.createCriteriaUpdate(Permission.class);
    Root<Permission> permissionRoot = criteriaUpdate.from(Permission.class);

    criteriaUpdate.set("deleted_at", null);
    criteriaUpdate.set("status", RoleStatus.ACTIVE.toString().toLowerCase());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.and(
        builder.equal(permissionRoot.get("id"), id),
        builder.isNotNull(permissionRoot.get("deleted_at"))));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("permission with ID %s not found or is not deleted.", id));

      throw new ResourceNotFoundException(String.format("permission with ID %s not found or is not deleted.", id));
    }

    entityManager.flush();
    entityManager.clear();

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully restore permission entity with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "permissions_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> delete(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Soft deleting permission entity with ID %s...", id));

    final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = iUser.findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found.", id)));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      throw new UnauthorizedException("Invalid or expired jwt.");
    }

    var data = UserDTO.fromObject(user);

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Permission> criteriaUpdate = builder.createCriteriaUpdate(Permission.class);
    Root<Permission> permissionRoot = criteriaUpdate.from(Permission.class);

    criteriaUpdate.set("deleted_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.set("status", RoleStatus.DELETED.toString().toLowerCase());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(permissionRoot.get("id"), id));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("permission with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("permission with ID %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully soft delete permission with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "permissions_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> forceDelete(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Force deleting permission entity with ID %s...", id));

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaDelete<Permission> criteriaDelete = builder.createCriteriaDelete(Permission.class);
    Root<Permission> permissionRoot = criteriaDelete.from(Permission.class);

    criteriaDelete.where(builder.equal(permissionRoot.get("id"), id));

    int deletedCount = entityManager.createQuery(criteriaDelete).executeUpdate();
    if (deletedCount != 1) {
      LOG.error(String.format("permission with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("permission with id %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully force delete permission entity with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
