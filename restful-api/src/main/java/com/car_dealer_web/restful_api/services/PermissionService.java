package com.car_dealer_web.restful_api.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.car_dealer_web.restful_api.dtos.permissions.PermissionJoinDTO;
import com.car_dealer_web.restful_api.dtos.permissions.PermissionWithRolesDTO;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.interfaces.IPermission;
import com.car_dealer_web.restful_api.interfaces.IPermissionService;
import com.car_dealer_web.restful_api.models.Permission;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.AttachRolesRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.CreatePermissionRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.DetachRolesRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.SyncRolesRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.UpdatePermissionRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PermissionService implements IPermissionService {
  private final IPermission iPermission;

  private final static Logger LOG = LoggerFactory.getLogger(PermissionService.class);

  public PermissionService(IPermission iPermission) {
    this.iPermission = iPermission;
  }

  @Override
  public ApiResponse<PaginationResponse<PermissionJoinDTO>> getAllPermissions(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("%s: processing request to get all permissions data.", getClass().getSimpleName()));

    try {
      PaginationResponse<PermissionJoinDTO> resource = iPermission.findAll(searchRequest, paginationRequest);

      ApiResponse<PaginationResponse<PermissionJoinDTO>> response = new ApiResponse<>(HttpStatus.OK.value(), true,
          "Succesfully fetch permissions.", DateTime.now(), httpServletRequest.getRequestURI(),
          resource);

      LOG.info(response.message());

      return response;
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ApiResponse<PermissionJoinDTO> getOnePermission(String id, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: processing request to get one permission with ID %s data.", getClass().getSimpleName(), id));

    try {
      PermissionJoinDTO resource = iPermission.findOne(id);

      ApiResponse<PermissionJoinDTO> response = new ApiResponse<>(HttpStatus.OK.value(), true,
          String.format("Succesfully fetch one permission with ID %s", id), DateTime.now(),
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
  public ApiResponse<Permission> createNewPermission(
      CreatePermissionRequest createPermissionRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: processing request to create one permission.", getClass().getSimpleName()));

    Permission resource = iPermission.save(createPermissionRequest);

    ApiResponse<Permission> response = new ApiResponse<>(HttpStatus.CREATED.value(), true,
        "Succesfully create one permission", DateTime.now(),
        httpServletRequest.getRequestURI(),
        resource);

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> modifyPermission(
      String id,
      UpdatePermissionRequest updatePermissionRequest,
      HttpServletRequest httpServletRequest) {
    int updatedRows = iPermission.update(id, updatePermissionRequest, httpServletRequest);

    if (updatedRows != 1) {
      LOG.error("Update failed. Permission with ID {} not found.", id);

      throw new ResourceNotFoundException(String.format("Permission with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<Object>(
        HttpStatus.OK.value(),
        true,
        String.format("Successfully updated permission with ID %s.", id),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> restorePermission(
      String id,
      HttpServletRequest httpServletRequest) {
    int restoredRows = iPermission.restore(id, httpServletRequest);

    if (restoredRows != 1) {
      LOG.error("Restore failed. Permission with ID {} not found.", id);

      throw new ResourceNotFoundException(String.format("Permission with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<Object>(
        HttpStatus.OK.value(),
        true,
        String.format("Successfully restored permission with ID %s.", id),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> deletePermission(
      String id,
      HttpServletRequest httpServletRequest) {
    int deletedRows = iPermission.delete(id, httpServletRequest);

    if (deletedRows != 1) {
      LOG.error("Soft delete failed. Permission with ID {} not found.", id);

      throw new ResourceNotFoundException(String.format("Permission with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<Object>(
        HttpStatus.OK.value(),
        true,
        String.format("Successfully soft deleted permission with ID %s.", id),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> forceDeletePermission(
      String id,
      HttpServletRequest httpServletRequest) {
    int forceDeleteRows = iPermission.forceDelete(id);

    if (forceDeleteRows != 1) {
      LOG.error("Force delete failed. Permission with ID {} not found.", id);

      throw new ResourceNotFoundException(String.format("Permission with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<Object>(
        HttpStatus.OK.value(),
        true,
        String.format("Successfully force deleted permission with ID %s.", id),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<PermissionWithRolesDTO> getOnePermissionWithRoles(
      String id,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Fetching roles from permission entity with ID %s resource.", getClass().getSimpleName(),
            id));

    try {
      PermissionWithRolesDTO resource = iPermission.fetchRoles(id);

      ApiResponse<PermissionWithRolesDTO> response = new ApiResponse<>(
          HttpStatus.OK.value(),
          true,
          String.format("Successfully fetch roles from permission entity with ID %s", id),
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
  public ApiResponse<Object> attachOnePermissionWithRoles(
      String id,
      AttachRolesRequest attachRolesRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Attaching permission with ID %s relation with roles.", getClass().getSimpleName(),
            id));

    iPermission.attachRoles(id, attachRolesRequest);

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        "Successfully attach relation with selected roles.", DateTime.now(), httpServletRequest.getRequestURI(),
        Map.of("ids", attachRolesRequest.roleIds()));

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> detachOnePermissionWithRoles(
      String id,
      DetachRolesRequest detachRolesRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Detaching permission with ID %s relation with roles.", getClass().getSimpleName(),
            id));

    iPermission.detachRoles(id, detachRolesRequest);

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        "Successfully detach relation with selected roles.", DateTime.now(), httpServletRequest.getRequestURI(),
        Map.of("ids", detachRolesRequest.roleIds()));

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> syncOnePermissionWithRoles(
      String id,
      SyncRolesRequest syncRolesRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Synchronize permission with ID %s relation with roles.", getClass().getSimpleName(),
            id));

    iPermission.syncRoles(id, syncRolesRequest);

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        "Successfully sync relation with selected roles.", DateTime.now(), httpServletRequest.getRequestURI(),
        Map.of("ids", syncRolesRequest.roleIds()));

    LOG.info(response.message());

    return response;
  }
}
