package com.car_dealer_web.restful_api.interfaces;

import com.car_dealer_web.restful_api.dtos.roles.RoleJoinDTO;
import com.car_dealer_web.restful_api.dtos.roles.RoleWithPermissionsDTO;
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

import jakarta.servlet.http.HttpServletRequest;

public interface IRoleService {
  ApiResponse<PaginationResponse<RoleJoinDTO>> getAllRoles(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest);

  ApiResponse<RoleJoinDTO> getOneRole(String id, HttpServletRequest httpServletRequest);

  ApiResponse<Role> createNewRole(CreateRoleRequest createRoleRequest, HttpServletRequest httpServletRequest);

  ApiResponse<Object> modifyRole(String id, UpdateRoleRequest updateRoleRequest, HttpServletRequest httpServletRequest);

  ApiResponse<Object> restoreRole(String id, HttpServletRequest httpServletRequest);

  ApiResponse<Object> deleteRole(String id, HttpServletRequest httpServletRequest);

  ApiResponse<Object> forceDeleteRole(String id, HttpServletRequest httpServletRequest);

  ApiResponse<RoleWithPermissionsDTO> getOneRoleWithPermissions(String id, HttpServletRequest httpServletRequest);

  ApiResponse<Object> attachOneRoleWithPermissions(String id, AttachPermissionsRequest attachPermissionsRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> detachOneRoleWithPermissions(String id, DetachPermissionsRequest detachPermissionsRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> syncOneRoleWithPermissions(String id, SyncPermissionsRequest syncPermissionsRequest,
      HttpServletRequest httpServletRequest);
}
