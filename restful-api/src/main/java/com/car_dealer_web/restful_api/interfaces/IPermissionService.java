package com.car_dealer_web.restful_api.interfaces;

import com.car_dealer_web.restful_api.dtos.permissions.PermissionJoinDTO;
import com.car_dealer_web.restful_api.dtos.permissions.PermissionWithRolesDTO;
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

import jakarta.servlet.http.HttpServletRequest;

public interface IPermissionService {
  ApiResponse<PaginationResponse<PermissionJoinDTO>> getAllPermissions(
      SearchRequest searchRequest,
      PaginationRequest paginationRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<PermissionJoinDTO> getOnePermission(
      String id,
      HttpServletRequest httpServletRequest);

  ApiResponse<Permission> createNewPermission(
      CreatePermissionRequest createPermissionRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> modifyPermission(
      String id,
      UpdatePermissionRequest updatePermissionRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> restorePermission(
      String id,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> deletePermission(
      String id,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> forceDeletePermission(
      String id,
      HttpServletRequest httpServletRequest);

  ApiResponse<PermissionWithRolesDTO> getOnePermissionWithRoles(
      String id,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> attachOnePermissionWithRoles(
      String id,
      AttachRolesRequest attachRolesRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> detachOnePermissionWithRoles(
      String id,
      DetachRolesRequest detachRolesRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> syncOnePermissionWithRoles(
      String id,
      SyncRolesRequest syncRolesRequest,
      HttpServletRequest httpServletRequest);
}
