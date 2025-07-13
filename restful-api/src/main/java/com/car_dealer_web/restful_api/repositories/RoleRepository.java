package com.car_dealer_web.restful_api.repositories;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
import com.car_dealer_web.restful_api.dtos.joins.RoleJoinDTO;
import com.car_dealer_web.restful_api.enums.RoleStatus;
import com.car_dealer_web.restful_api.exceptions.BadRequestException;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.exceptions.UnauthorizedException;
import com.car_dealer_web.restful_api.handlers.JwtAuthHandler;
import com.car_dealer_web.restful_api.interfaces.IRole;
import com.car_dealer_web.restful_api.interfaces.IUser;
import com.car_dealer_web.restful_api.models.Role;
import com.car_dealer_web.restful_api.models.User;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.CreateRoleRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.UpdateRoleRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

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
public class RoleRepository implements IRole {
  private final IUser iUser;
  private final EntityManager entityManager;
  private final JwtAuthHandler jwtAuthHandler;
  private final static Logger LOG = LoggerFactory.getLogger(RoleRepository.class);

  public RoleRepository(
      IUser iUser,
      EntityManager entityManager,
      JwtAuthHandler jwtAuthHandler) {
    this.iUser = iUser;
    this.entityManager = entityManager;
    this.jwtAuthHandler = jwtAuthHandler;
  }

  private final void searchRequestPredicate(
      SearchRequest request, List<Predicate> predicates, CriteriaBuilder builder, Root<Role> root,
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
  @Cacheable(value = "roles_cache")
  public ResponseEntity<ApiResponse<PaginationResponse<RoleJoinDTO>>> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest) {
    LOG.info("Fetching all role entity resources...");

    try {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();

      // ------------------COUNT QUERY-----------------------
      CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
      Root<Role> countRoleRoot = countQuery.from(Role.class);
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
      Root<Role> selectRoleRoot = selectQuery.from(Role.class);
      Join<Role, User> selectJoinWithUser = selectRoleRoot.join("users", JoinType.INNER);
      List<Predicate> selectPredicates = new ArrayList<>();

      // WHERE DELETED_AT IS NULL.
      selectPredicates.add(builder.isNull(selectRoleRoot.get("deleted_at")));

      // CASE WHEN SEARCH REQUEST INCLUDED.
      searchRequestPredicate(
          searchRequest,
          countPredicates,
          builder,
          selectRoleRoot,
          Arrays.asList("name"));

      selectQuery.multiselect(
          // ROLES SELECTED COLUMNS.
          selectRoleRoot.get("id").alias("id"),
          selectRoleRoot.get("name").alias("name"),
          selectRoleRoot.get("description").alias("description"),
          selectRoleRoot.get("status").alias("status"),
          selectRoleRoot.get("last_edited_by").alias("last_edited_by"),
          selectRoleRoot.get("created_at").alias("created_at"),
          selectRoleRoot.get("updated_at").alias("updated_at"),
          selectRoleRoot.get("deleted_at").alias("deleted_at"),

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
          selectJoinWithUser.get("last_login_at").alias("user_last_login_at"))
          .distinct(true)
          .where(builder.and(selectPredicates.toArray(Predicate[]::new)));

      // SORT DATA.
      if (!"desc".equalsIgnoreCase(paginationRequest.direction())) {
        selectQuery.orderBy(
            builder.asc(selectRoleRoot.get(paginationRequest.sortField())));
      } else {
        selectQuery.orderBy(
            builder.desc(selectRoleRoot.get(paginationRequest.sortField())));
      }

      TypedQuery<Tuple> typedQuery = entityManager.createQuery(selectQuery);

      // OFFSET DATA.
      typedQuery.setFirstResult((paginationRequest.page() - 1) * paginationRequest.size());

      // LIMIT DATA.
      typedQuery.setMaxResults(paginationRequest.size());

      // CONVERT INTO DTO.
      List<Tuple> result = typedQuery.getResultList();
      List<RoleJoinDTO> roleJoinWithOthers = result.stream()
          .map(RoleJoinDTO::fromTuple)
          .toList();

      // SETUP PAGINATION RESPONSE.
      var totalPages = (int) Math.ceil((double) totalElements / paginationRequest.size());
      var hasNext = paginationRequest.page() < totalPages;
      PaginationResponse<RoleJoinDTO> resource = new PaginationResponse<>(
          roleJoinWithOthers,
          totalPages,
          totalElements,
          paginationRequest.size(),
          paginationRequest.page(),
          hasNext);

      var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
      var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      ApiResponse<PaginationResponse<RoleJoinDTO>> response = new ApiResponse<>(
          HttpStatus.OK.value(),
          true,
          "Succesfully fetch roles.",
          now.format(formatter),
          resource);

      LOG.info(response.message());

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  @Cacheable(value = "roles_cache", key = "#id")
  public ResponseEntity<ApiResponse<RoleJoinDTO>> findOne(String id) {
    LOG.info(String.format("Fetching role entity with ID %s resource...", id));

    try {
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Tuple> selectQuery = builder.createQuery(Tuple.class);
      Root<Role> selectRoleRoot = selectQuery.from(Role.class);
      Join<Role, User> selectJoinWithUser = selectRoleRoot.join("users", JoinType.INNER);
      List<Predicate> selectPredicates = new ArrayList<>();

      // WHERE DELETED_AT IS NULL.
      selectPredicates.add(builder.isNull(selectRoleRoot.get("deleted_at")));

      selectQuery.multiselect(
          // ROLES SELECTED COLUMNS.
          selectRoleRoot.get("id").alias("id"),
          selectRoleRoot.get("name").alias("name"),
          selectRoleRoot.get("description").alias("description"),
          selectRoleRoot.get("status").alias("status"),
          selectRoleRoot.get("last_edited_by").alias("last_edited_by"),
          selectRoleRoot.get("created_at").alias("created_at"),
          selectRoleRoot.get("updated_at").alias("updated_at"),
          selectRoleRoot.get("deleted_at").alias("deleted_at"),

          // USERS SELECTED COLUMNS.
          selectJoinWithUser.get("id").alias("user_id"),
          selectJoinWithUser.get("fullname").alias("user_fullname"),
          selectJoinWithUser.get("bio").alias("user_bio"),
          selectJoinWithUser.get("email").alias("user_email"),
          selectJoinWithUser.get("phone_number").alias("user_phone_number"),
          selectJoinWithUser.get("address").alias("user_address"),
          selectJoinWithUser.get("account_status").alias("user_account_status"),
          selectJoinWithUser.get("active_status").alias("user_active_status"),
          selectJoinWithUser.get("avatar_url").alias("user_avatar_url"))
          .distinct(true)
          .where(builder.and(
              builder.and(selectPredicates.toArray(Predicate[]::new)),
              builder.equal(selectRoleRoot.get("id"), id)));

      // CONVERT INTO DTO.
      TypedQuery<Tuple> typedQuery = entityManager.createQuery(selectQuery);
      RoleJoinDTO resource = RoleJoinDTO.fromTuple(typedQuery.getSingleResult());

      var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
      var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      ApiResponse<RoleJoinDTO> response = new ApiResponse<>(HttpStatus.OK.value(), true,
          String.format("Successfully fetch role with ID %s", id),
          now.format(formatter),
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
  @CachePut(value = "roles_cache", key = "#result.resource.id")
  public ResponseEntity<ApiResponse<Role>> save(CreateRoleRequest request) {
    LOG.info("Creating new role...");

    Role role = new Role();
    role.setName(request.name());
    role.setDescription(request.description());
    role.setStatus(RoleStatus.ACTIVE.toString().toLowerCase());

    entityManager.persist(role);
    entityManager.flush();

    // SETUP THE API (JSON) RESPONSE.
    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ApiResponse<Role> response = new ApiResponse<>(HttpStatus.OK.value(), true, "Successfully add new role.",
        now.format(formatter),
        role);

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  @Transactional
  @CacheEvict(value = "roles_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> update(String id, UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(String.format("Updating role entity with ID %s...", id));

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
    CriteriaUpdate<Role> criteriaUpdate = builder.createCriteriaUpdate(Role.class);
    Root<Role> roleRoot = criteriaUpdate.from(Role.class);

    criteriaUpdate.set("name", updateRoleRequest.name());
    criteriaUpdate.set("description", updateRoleRequest.description());
    criteriaUpdate.set("status", updateRoleRequest.status());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(roleRoot.get("id"), id));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("role with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("role with ID %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully update role entity with ID %s.", id),
        now.format(formatter), new HashMap<>());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "roles_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> restore(String id) {
    LOG.info(String.format("Restoring role entity with ID %s...", id));

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Role> criteriaUpdate = builder.createCriteriaUpdate(Role.class);
    Root<Role> roleRoot = criteriaUpdate.from(Role.class);

    criteriaUpdate.set("deleted_at", null);
    criteriaUpdate.where(builder.and(
        builder.equal(roleRoot.get("id"), id),
        builder.isNotNull(roleRoot.get("deleted_at"))));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("role with ID %s not found or is not deleted.", id));

      throw new ResourceNotFoundException(String.format("role with ID %s not found or is not deleted.", id));
    }

    entityManager.flush();
    entityManager.clear();

    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully restore role entity with ID %s", id),
        now.format(formatter), new HashMap<>());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "roles_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> delete(String id) {
    LOG.info(String.format("Soft deleting role entity with ID %s...", id));

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Role> criteriaUpdate = builder.createCriteriaUpdate(Role.class);
    Root<Role> roleRoot = criteriaUpdate.from(Role.class);

    criteriaUpdate.set("deleted_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(roleRoot.get("id"), id));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();
    if (updated != 1) {
      LOG.error(String.format("role with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("role with ID %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully soft delete role with ID %s", id),
        now.format(formatter), new HashMap<>());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  @CacheEvict(value = "roles_cache", key = "#id")
  public ResponseEntity<ApiResponse<Object>> forceDelete(String id) {
    LOG.info(String.format("Force deleting role entity with ID %s...", id));

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaDelete<Role> criteriaDelete = builder.createCriteriaDelete(Role.class);
    Root<Role> roleRoot = criteriaDelete.from(Role.class);

    criteriaDelete.where(builder.equal(roleRoot.get("id"), id));

    int deletedCount = entityManager.createQuery(criteriaDelete).executeUpdate();
    if (deletedCount != 1) {
      LOG.error(String.format("role with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("role with id %s not found.", id));
    }

    entityManager.flush();
    entityManager.clear();

    var now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully force delete role entity with ID %s", id),
        now.format(formatter), new HashMap<>());

    LOG.info(response.message());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
