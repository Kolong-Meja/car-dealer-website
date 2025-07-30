package com.car_dealer_web.restful_api.repositories;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;

import com.car_dealer_web.restful_api.dtos.roles.RoleJoinDTO;
import com.car_dealer_web.restful_api.dtos.roles.RoleWithPermissionsDTO;
import com.car_dealer_web.restful_api.dtos.users.UserDTO;
import com.car_dealer_web.restful_api.enums.RoleStatus;
import com.car_dealer_web.restful_api.exceptions.BadRequestException;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.exceptions.UnauthorizedException;
import com.car_dealer_web.restful_api.handlers.JwtAuthHandler;
import com.car_dealer_web.restful_api.interfaces.IRole;
import com.car_dealer_web.restful_api.interfaces.IUser;
import com.car_dealer_web.restful_api.models.Permission;
import com.car_dealer_web.restful_api.models.Role;
import com.car_dealer_web.restful_api.models.User;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.AttachPermissionsRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.CreateRoleRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.DetachPermissionsRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.SyncPermissionsRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.UpdateRoleRequest;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

import jakarta.persistence.EntityManager;
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
  @Cacheable(cacheNames = "roles_cache", key = "'all'")
  public PaginationResponse<RoleJoinDTO> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest) {
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
    Join<Role, Permission> selectJoinWithPermission = selectRoleRoot.join("permissions", JoinType.INNER);
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
        selectJoinWithUser.get("last_login_at").alias("user_last_login_at"),

        // PERMISSION SELECTED COLUMNS.
        selectJoinWithPermission.get("id").alias("permission_id"),
        selectJoinWithPermission.get("name").alias("permission_name"),
        selectJoinWithPermission.get("description").alias("permission_description"),
        selectJoinWithPermission.get("status").alias("permission_status"))
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

    return resource;
  }

  @Override
  @Cacheable(cacheNames = "roles_cache", key = "#id")
  public RoleJoinDTO findOne(String id) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> selectQuery = builder.createQuery(Tuple.class);
    Root<Role> selectRoleRoot = selectQuery.from(Role.class);
    Join<Role, User> selectJoinWithUser = selectRoleRoot.join("users", JoinType.INNER);
    Join<Role, Permission> selectJoinWithPermission = selectRoleRoot.join("permissions", JoinType.INNER);
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
        selectJoinWithUser.get("avatar_url").alias("user_avatar_url"),

        // PERMISSION SELECTED COLUMNS.
        selectJoinWithPermission.get("id").alias("permission_id"),
        selectJoinWithPermission.get("name").alias("permission_name"),
        selectJoinWithPermission.get("description").alias("permission_description"),
        selectJoinWithPermission.get("status").alias("permission_status"))
        .distinct(true)
        .where(builder.and(
            builder.and(selectPredicates.toArray(Predicate[]::new)),
            builder.equal(selectRoleRoot.get("id"), id)));

    // CONVERT INTO DTO.
    TypedQuery<Tuple> typedQuery = entityManager.createQuery(selectQuery);
    RoleJoinDTO resource = RoleJoinDTO.fromTuple(typedQuery.getSingleResult());

    return resource;
  }

  @Override
  @Transactional
  @CachePut(value = "roles_cache", key = "#role.id")
  public Role save(CreateRoleRequest createRoleRequest) {
    Role role = new Role();
    role.setName(createRoleRequest.name());
    role.setDescription(createRoleRequest.description());
    role.setStatus(RoleStatus.ACTIVE.toString().toLowerCase());

    entityManager.persist(role);
    entityManager.flush();

    return role;
  }

  @Override
  @Transactional
  @CacheEvict(value = "roles_cache", key = "#id")
  public int update(String id, UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest) {
    final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      LOG.error("Missing or invalid Authorization header", BadRequestException.class);

      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = iUser.findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found.", id)));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      LOG.error("Invalid or expired jwt.", UnauthorizedException.class);

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

    if (updated > 0) {
      entityManager.flush();
      entityManager.clear();
    }

    return updated;
  }

  @Override
  @Transactional
  @CacheEvict(value = "roles_cache", key = "#id")
  public int restore(String id, HttpServletRequest httpServletRequest) {
    final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      LOG.error("Missing or invalid Authorization header", BadRequestException.class);

      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = iUser.findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found.", id)));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      LOG.error("Invalid or expired jwt.", UnauthorizedException.class);

      throw new UnauthorizedException("Invalid or expired jwt.");
    }

    var data = UserDTO.fromObject(user);

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Role> criteriaUpdate = builder.createCriteriaUpdate(Role.class);
    Root<Role> roleRoot = criteriaUpdate.from(Role.class);

    criteriaUpdate.set("deleted_at", null);
    criteriaUpdate.set("status", RoleStatus.ACTIVE.toString().toLowerCase());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.and(
        builder.equal(roleRoot.get("id"), id),
        builder.isNotNull(roleRoot.get("deleted_at"))));

    int restored = entityManager.createQuery(criteriaUpdate).executeUpdate();

    if (restored > 0) {
      entityManager.flush();
      entityManager.clear();
    }

    return restored;
  }

  @Override
  @Transactional
  @CacheEvict(value = "roles_cache", key = "#id")
  public int delete(String id, HttpServletRequest httpServletRequest) {
    final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      LOG.error("Missing or invalid Authorization header", BadRequestException.class);

      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    var user = iUser.findOneByEmail(userEmail)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found.", id)));

    if (!jwtAuthHandler.isTokenValid(jwt, user)) {
      LOG.error("Invalid or expired jwt.", UnauthorizedException.class);

      throw new UnauthorizedException("Invalid or expired jwt.");
    }

    var data = UserDTO.fromObject(user);

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Role> criteriaUpdate = builder.createCriteriaUpdate(Role.class);
    Root<Role> roleRoot = criteriaUpdate.from(Role.class);

    criteriaUpdate.set("deleted_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.set("status", RoleStatus.DELETED.toString().toLowerCase());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(roleRoot.get("id"), id));

    int softDeleted = entityManager.createQuery(criteriaUpdate).executeUpdate();

    if (softDeleted > 0) {
      entityManager.flush();
      entityManager.clear();
    }

    return softDeleted;
  }

  @Override
  @Transactional
  @CacheEvict(value = "roles_cache", key = "#id")
  public int forceDelete(String id) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaDelete<Role> criteriaDelete = builder.createCriteriaDelete(Role.class);
    Root<Role> roleRoot = criteriaDelete.from(Role.class);

    criteriaDelete.where(builder.equal(roleRoot.get("id"), id));

    int forceDeleted = entityManager.createQuery(criteriaDelete).executeUpdate();

    if (forceDeleted > 0) {
      entityManager.flush();
      entityManager.clear();
    }

    return forceDeleted;
  }

  @Override
  public RoleWithPermissionsDTO fetchPermissions(String id) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Role> selectQuery = builder.createQuery(Role.class);
    Root<Role> selectRoleRoot = selectQuery.from(Role.class);
    List<Predicate> selectPredicates = new ArrayList<>();

    // FETCH PERMISSiONS.
    selectRoleRoot.fetch("permissions", JoinType.INNER);

    // WHERE DELETED_AT IS NULL.
    selectPredicates.add(builder.isNull(selectRoleRoot.get("deleted_at")));

    selectQuery.select(selectRoleRoot)
        .distinct(true)
        .where(builder.and(
            builder.and(selectPredicates.toArray(Predicate[]::new)),
            builder.equal(selectRoleRoot.get("id"), id)));

    TypedQuery<Role> typedQuery = entityManager.createQuery(selectQuery);
    RoleWithPermissionsDTO resource = RoleWithPermissionsDTO
        .fromObject(typedQuery.getSingleResult());

    return resource;
  }

  @Override
  @Transactional
  @CachePut(value = "role_permissions_cache", key = "#id")
  public void attachPermissions(String id,
      AttachPermissionsRequest attachPermissionsRequest) {
    Role role = entityManager.find(Role.class, id);

    if (role == null) {
      LOG.error("Role not found.", ResourceNotFoundException.class);

      throw new ResourceNotFoundException("Role not found.");
    }

    Hibernate.initialize(role.getPermissions());

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Permission> selectPermissionQuery = builder.createQuery(Permission.class);
    Root<Permission> selectPermissionRoot = selectPermissionQuery.from(Permission.class);

    List<Predicate> selectPredicates = new ArrayList<>();
    selectPredicates.add(builder.isNull(selectPermissionRoot.get("deleted_at")));

    selectPermissionQuery.select(selectPermissionRoot)
        .where(selectPermissionRoot.get("id").in(attachPermissionsRequest.permissionIds()));

    List<Permission> permissions = entityManager.createQuery(selectPermissionQuery).getResultList();

    if (permissions.size() != attachPermissionsRequest.permissionIds().size()) {
      LOG.error("No single permissions found.", BadRequestException.class);

      throw new BadRequestException("No single permissions found.");
    }

    Set<Permission> selectedPermissions = new HashSet<>(permissions);

    for (Permission permission : selectedPermissions) {
      if (!role.getPermissions().contains(permission)) {
        role.getPermissions().add(permission);
      }
    }

    entityManager.persist(role);
    entityManager.flush();
  }

  @Override
  @Transactional
  @CachePut(value = "role_permissions_cache", key = "#id")
  public void detachPermissions(String id,
      DetachPermissionsRequest detachPermissionsRequest) {
    LOG.info("Detaching relation with permissions...");

    Role role = entityManager.find(Role.class, id);

    if (role == null) {
      LOG.error("Role not found.", ResourceNotFoundException.class);

      throw new ResourceNotFoundException("Role not found.");
    }

    Hibernate.initialize(role.getPermissions());

    boolean isDetached = role.getPermissions()
        .removeIf(permission -> detachPermissionsRequest.permissionIds().contains(permission.getId()));

    if (!isDetached) {
      LOG.error("No matching permissions to detach.", BadRequestException.class);

      throw new BadRequestException("No matching permissions to detach.");
    }

    entityManager.merge(role);
    entityManager.flush();
    entityManager.clear();
  }

  @Override
  @Transactional
  @CachePut(value = "role_permissions_cache", key = "#id")
  public void syncPermissions(String id,
      SyncPermissionsRequest syncPermissionsRequest) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();

    // CHECK THE PERMISSION IDS FIRST.
    CriteriaQuery<Permission> selectPermissionQuery = builder.createQuery(Permission.class);
    Root<Permission> selectPermissionRoot = selectPermissionQuery.from(Permission.class);

    List<Predicate> selectPredicates = new ArrayList<>();
    selectPredicates.add(builder.isNull(selectPermissionRoot.get("deleted_at")));

    selectPermissionQuery.select(selectPermissionRoot)
        .where(selectPermissionRoot.get("id").in(syncPermissionsRequest.permissionIds()));

    List<Permission> permissions = entityManager.createQuery(selectPermissionQuery).getResultList();

    if (permissions.size() != syncPermissionsRequest.permissionIds().size()) {
      LOG.error("No single permissions found.", BadRequestException.class);

      throw new BadRequestException("No single permissions found.");
    }

    Set<Permission> selectedPermissions = new HashSet<>(permissions);

    // ATTACH THE RELATIONS.
    Role role = entityManager.find(Role.class, id);

    if (role == null) {
      LOG.error("Role not found.", ResourceNotFoundException.class);

      throw new ResourceNotFoundException("Role not found.");
    }

    role.setPermissions(selectedPermissions);

    entityManager.merge(role);
    entityManager.flush();
    entityManager.clear();
  }
}
