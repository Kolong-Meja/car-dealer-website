package com.car_dealer_web.restful_api.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.car_dealer_web.restful_api.dtos.roles.RoleJoinDTO;
import com.car_dealer_web.restful_api.dtos.roles.RoleWithPermissionsDTO;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.interfaces.IRole;
import com.car_dealer_web.restful_api.interfaces.IRoleService;
import com.car_dealer_web.restful_api.models.Role;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.AttachPermissionsRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.CreateRoleRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.DetachPermissionsRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.SyncPermissionsRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.UpdateRoleRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class RoleService implements IRoleService {
  private final IRole iRole;

  private final static Logger LOG = LoggerFactory.getLogger(RoleService.class);

  public RoleService(IRole iRole) {
    this.iRole = iRole;
  }

  @Override
  public ApiResponse<PaginationResponse<RoleJoinDTO>> getAllRoles(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("%s: Processing request to get all roles data.", getClass().getSimpleName()));

    try {
      PaginationResponse<RoleJoinDTO> resource = iRole.findAll(searchRequest, paginationRequest);

      ApiResponse<PaginationResponse<RoleJoinDTO>> response = new ApiResponse<>(HttpStatus.OK.value(), true,
          "Succesfully fetch roles.", DateTime.now(), httpServletRequest.getRequestURI(),
          resource);

      LOG.info(response.message());

      return response;
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ApiResponse<RoleJoinDTO> getOneRole(String id, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to get one role with ID %s data.", getClass().getSimpleName(), id));

    try {
      RoleJoinDTO resource = iRole.findOne(id);

      ApiResponse<RoleJoinDTO> response = new ApiResponse<>(HttpStatus.OK.value(), true,
          String.format("Succesfully fetch one role with ID %s", id), DateTime.now(),
          httpServletRequest.getRequestURI(),
          resource);

      LOG.info(response.message());

      return response;
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ApiResponse<Role> createNewRole(CreateRoleRequest createRoleRequest, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to create one role.", getClass().getSimpleName()));

    Role resource = iRole.save(createRoleRequest);

    ApiResponse<Role> response = new ApiResponse<>(HttpStatus.CREATED.value(), true,
        "Succesfully create one role", DateTime.now(),
        httpServletRequest.getRequestURI(),
        resource);

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> modifyRole(String id, UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to update one role data with ID %s.", getClass().getSimpleName(), id));

    int updatedRows = iRole.update(id, updateRoleRequest, httpServletRequest);

    if (updatedRows != 1) {
      LOG.error("Update failed. Role with ID {} not found.", id);

      throw new ResourceNotFoundException(String.format("Role with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<Object>(
        HttpStatus.OK.value(),
        true,
        String.format("Successfully updated role with ID %s.", id),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> restoreRole(String id, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to restore one user data with ID %s.", getClass().getSimpleName(), id));

    int restoredRows = iRole.restore(id, httpServletRequest);

    if (restoredRows != 1) {
      LOG.error("Restore failed. Role with ID {} not found.", id);

      throw new ResourceNotFoundException(String.format("Role with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<Object>(
        HttpStatus.OK.value(),
        true,
        String.format("Successfully restored role with ID %s.", id),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> deleteRole(String id, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to delete one user data with ID %s.", getClass().getSimpleName(), id));

    int deletedRows = iRole.delete(id, httpServletRequest);

    if (deletedRows != 1) {
      LOG.error("Soft delete failed. Role with ID {} not found.", id);

      throw new ResourceNotFoundException(String.format("Role with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<Object>(
        HttpStatus.OK.value(),
        true,
        String.format("Successfully soft deleted role with ID %s.", id),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> forceDeleteRole(String id, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to force delete one user data with ID %s.", getClass().getSimpleName(),
            id));

    int forceDeleteRows = iRole.forceDelete(id);

    if (forceDeleteRows != 1) {
      LOG.error("Force delete failed. Role with ID {} not found.", id);

      throw new ResourceNotFoundException(String.format("Role with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<Object>(
        HttpStatus.OK.value(),
        true,
        String.format("Successfully force deleted role with ID %s.", id),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<RoleWithPermissionsDTO> getOneRoleWithPermissions(String id,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Fetching permissions from role entity with ID %s resource.", getClass().getSimpleName(),
            id));

    try {
      RoleWithPermissionsDTO resource = iRole.fetchPermissions(id);

      ApiResponse<RoleWithPermissionsDTO> response = new ApiResponse<>(
          HttpStatus.OK.value(),
          true,
          String.format("Successfully fetch permissions from role entity with ID %s", id),
          DateTime.now(),
          httpServletRequest.getRequestURI(),
          resource);

      LOG.info(response.message());

      return response;
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ApiResponse<Object> attachOneRoleWithPermissions(String id, AttachPermissionsRequest attachPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Attaching role with ID %s relation with permissions.", getClass().getSimpleName(),
            id));

    iRole.attachPermissions(id, attachPermissionsRequest);

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        "Successfully attach relation with selected permissions.", DateTime.now(), httpServletRequest.getRequestURI(),
        Map.of("ids", attachPermissionsRequest.permissionIds()));

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> detachOneRoleWithPermissions(String id, DetachPermissionsRequest detachPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Detaching role with ID %s relation with permissions.", getClass().getSimpleName(),
            id));

    iRole.detachPermissions(id, detachPermissionsRequest);

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        "Successfully detach relation with selected permissions.", DateTime.now(), httpServletRequest.getRequestURI(),
        Map.of("ids", detachPermissionsRequest.permissionIds()));

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> syncOneRoleWithPermissions(String id, SyncPermissionsRequest syncPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Synchronize permission with ID %s relation with roles.", getClass().getSimpleName(),
            id));

    iRole.syncPermissions(id, syncPermissionsRequest);

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        "Successfully sync relation with selected permissions.", DateTime.now(), httpServletRequest.getRequestURI(),
        Map.of("ids", syncPermissionsRequest.permissionIds()));

    LOG.info(response.message());

    return response;
  }

}
