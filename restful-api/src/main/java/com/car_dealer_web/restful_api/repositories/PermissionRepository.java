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
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;

import com.car_dealer_web.restful_api.dtos.permissions.PermissionJoinDTO;
import com.car_dealer_web.restful_api.dtos.permissions.PermissionWithRolesDTO;
import com.car_dealer_web.restful_api.dtos.users.UserDTO;
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
import com.car_dealer_web.restful_api.payloads.requests.permissions.AttachRolesRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.CreatePermissionRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.DetachRolesRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.SyncRolesRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.UpdatePermissionRequest;
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
public class PermissionRepository implements IPermission {
  private final IUser iUser;
  private final EntityManager entityManager;
  private final JwtAuthHandler jwtAuthHandler;
  private final static Logger LOG = LoggerFactory.getLogger(PermissionRepository.class);

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
  @Cacheable(cacheNames = "permissions_cache", key = "'all'")
  public PaginationResponse<PermissionJoinDTO> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest) {
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

    return resource;
  }

  @Override
  @Cacheable(cacheNames = "permissions_cache", key = "#id")
  public PermissionJoinDTO findOne(String id) {
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

    return resource;
  }

  @Override
  @Transactional
  @Caching(put = {
      @CachePut(cacheNames = "permissions_cache", key = "#permission.id")
  }, evict = {
      @CacheEvict(cacheNames = "permissions_cache", key = "'all'")
  })
  public Permission save(CreatePermissionRequest createPermissionRequest) {
    Permission permission = new Permission();
    permission.setName(createPermissionRequest.name());
    permission.setDescription(createPermissionRequest.description());
    permission.setStatus(RoleStatus.ACTIVE.toString().toLowerCase());

    entityManager.persist(permission);
    entityManager.flush();

    return permission;
  }

  @Override
  @Transactional
  @Caching(put = {
      @CachePut(cacheNames = "permissions_cache", key = "#id")
  }, evict = {
      @CacheEvict(cacheNames = "permissions_cache", key = "'all'")
  })
  public int update(
      String id,
      UpdatePermissionRequest updatePermissionRequest,
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
    CriteriaUpdate<Permission> criteriaUpdate = builder.createCriteriaUpdate(Permission.class);
    Root<Permission> permissionRoot = criteriaUpdate.from(Permission.class);

    criteriaUpdate.set("name", updatePermissionRequest.name());
    criteriaUpdate.set("description", updatePermissionRequest.description());
    criteriaUpdate.set("status", updatePermissionRequest.status());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(permissionRoot.get("id"), id));

    int updated = entityManager.createQuery(criteriaUpdate).executeUpdate();

    if (updated > 0) {
      entityManager.flush();
      entityManager.clear();
    }

    return updated;
  }

  @Override
  @Transactional
  @Caching(put = {
      @CachePut(cacheNames = "permissions_cache", key = "#id")
  }, evict = {
      @CacheEvict(cacheNames = "permissions_cache", key = "'all'")
  })
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
    CriteriaUpdate<Permission> criteriaUpdate = builder.createCriteriaUpdate(Permission.class);
    Root<Permission> permissionRoot = criteriaUpdate.from(Permission.class);

    criteriaUpdate.set("deleted_at", null);
    criteriaUpdate.set("status", RoleStatus.ACTIVE.toString().toLowerCase());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.and(
        builder.equal(permissionRoot.get("id"), id),
        builder.isNotNull(permissionRoot.get("deleted_at"))));

    int restored = entityManager.createQuery(criteriaUpdate).executeUpdate();

    if (restored > 0) {
      entityManager.flush();
      entityManager.clear();
    }

    return restored;
  }

  @Override
  @Transactional
  @Caching(put = {
      @CachePut(cacheNames = "permissions_cache", key = "#id")
  }, evict = {
      @CacheEvict(cacheNames = "permissions_cache", key = "'all'")
  })
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
    CriteriaUpdate<Permission> criteriaUpdate = builder.createCriteriaUpdate(Permission.class);
    Root<Permission> permissionRoot = criteriaUpdate.from(Permission.class);

    criteriaUpdate.set("deleted_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.set("status", RoleStatus.DELETED.toString().toLowerCase());
    criteriaUpdate.set("last_edited_by", data.id());
    criteriaUpdate.set("updated_at", LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
    criteriaUpdate.where(builder.equal(permissionRoot.get("id"), id));

    int softDeleted = entityManager.createQuery(criteriaUpdate).executeUpdate();

    if (softDeleted > 0) {
      entityManager.flush();
      entityManager.clear();
    }

    return softDeleted;
  }

  @Override
  @Transactional
  @Caching(evict = {
      @CacheEvict(cacheNames = "permissions_cache", key = "#id"),
      @CacheEvict(cacheNames = "permissions_cache", key = "'all'")
  })
  public int forceDelete(String id) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaDelete<Permission> criteriaDelete = builder.createCriteriaDelete(Permission.class);
    Root<Permission> permissionRoot = criteriaDelete.from(Permission.class);

    criteriaDelete.where(builder.equal(permissionRoot.get("id"), id));

    int forceDeleted = entityManager.createQuery(criteriaDelete).executeUpdate();

    if (forceDeleted > 0) {
      entityManager.flush();
      entityManager.clear();
    }

    return forceDeleted;
  }

  @Override
  @Transactional
  @Cacheable(value = "permission_roles_cache", key = "#id")
  public PermissionWithRolesDTO fetchRoles(String id) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Permission> selectQuery = builder.createQuery(Permission.class);
    Root<Permission> selectPermissionRoot = selectQuery.from(Permission.class);
    List<Predicate> selectPredicates = new ArrayList<>();

    // FETCH ROLES.
    selectPermissionRoot.fetch("roles", JoinType.INNER);

    // WHERE DELETED_AT IS NULL.
    selectPredicates.add(builder.isNull(selectPermissionRoot.get("deleted_at")));

    selectQuery.select(selectPermissionRoot)
        .distinct(true)
        .where(builder.and(
            builder.and(selectPredicates.toArray(Predicate[]::new)),
            builder.equal(selectPermissionRoot.get("id"), id)));

    TypedQuery<Permission> typedQuery = entityManager.createQuery(selectQuery);
    PermissionWithRolesDTO resource = PermissionWithRolesDTO
        .fromObject(typedQuery.getSingleResult());

    return resource;
  }

  @Override
  @Transactional
  @CachePut(cacheNames = "permission_roles_cache", key = "#id")
  public void attachRoles(String id, AttachRolesRequest attachRolesRequest) {
    Permission permission = entityManager.find(Permission.class, id);

    if (permission == null) {
      LOG.error("Permission not found.", ResourceNotFoundException.class);

      throw new ResourceNotFoundException("Permission not found.");
    }

    Hibernate.initialize(permission.getRoles());

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Role> selectRoleQuery = builder.createQuery(Role.class);
    Root<Role> selectRoleRoot = selectRoleQuery.from(Role.class);

    List<Predicate> selectPredicates = new ArrayList<>();
    selectPredicates.add(builder.isNull(selectRoleRoot.get("deleted_at")));

    selectRoleQuery.select(selectRoleRoot)
        .where(selectRoleRoot.get("id").in(attachRolesRequest.roleIds()));

    List<Role> roles = entityManager.createQuery(selectRoleQuery).getResultList();

    if (roles.size() != attachRolesRequest.roleIds().size()) {
      LOG.error("Not single roles found.", BadRequestException.class);

      throw new BadRequestException("Not single roles found.");
    }

    Set<Role> selectedRoles = new HashSet<>(roles);

    for (Role role : selectedRoles) {
      if (!permission.getRoles().contains(role)) {
        permission.getRoles().add(role);
      }
    }

    entityManager.persist(permission);
    entityManager.flush();
  }

  @Override
  @Transactional
  @CachePut(cacheNames = "permission_roles_cache", key = "#id")
  public void detachRoles(String id, DetachRolesRequest detachRolesRequest) {
    Permission permission = entityManager.find(Permission.class, id);

    if (permission == null) {
      LOG.error("Permission not found.", ResourceNotFoundException.class);

      throw new ResourceNotFoundException("Permission not found.");
    }

    Hibernate.initialize(permission.getRoles());

    boolean isDetached = permission.getRoles()
        .removeIf(role -> detachRolesRequest.roleIds().contains(role.getId()));

    if (!isDetached) {
      LOG.error("No matching roles to detach.", BadRequestException.class);

      throw new BadRequestException("No matching roles to detach.");
    }

    entityManager.merge(permission);
    entityManager.flush();
    entityManager.clear();
  }

  @Override
  @Transactional
  @CachePut(cacheNames = "permission_roles_cache", key = "#id")
  public void syncRoles(String id, SyncRolesRequest syncRolesRequest) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();

    // CHECK THE ROLES IDS FIRST.
    CriteriaQuery<Role> selectRoleQuery = builder.createQuery(Role.class);
    Root<Role> selectRoleRoot = selectRoleQuery.from(Role.class);

    List<Predicate> selectPredicates = new ArrayList<>();
    selectPredicates.add(builder.isNull(selectRoleRoot.get("deleted_at")));

    selectRoleQuery.select(selectRoleRoot)
        .where(selectRoleRoot.get("id").in(syncRolesRequest.roleIds()));

    List<Role> roles = entityManager.createQuery(selectRoleQuery).getResultList();

    if (roles.size() != syncRolesRequest.roleIds().size()) {
      LOG.error("Not single roles found.", BadRequestException.class);

      throw new BadRequestException("Not single roles found.");
    }

    Set<Role> selectedRoles = new HashSet<>(roles);

    // ATTACH THE RELATIONS.
    Permission permission = entityManager.find(Permission.class, id);

    if (permission == null) {
      LOG.error("Permission not found.", ResourceNotFoundException.class);

      throw new ResourceNotFoundException("Permission not found.");
    }

    permission.setRoles(selectedRoles);

    entityManager.merge(permission);
    entityManager.flush();
    entityManager.clear();
  }
}
