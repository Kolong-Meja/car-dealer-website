package com.car_dealer_web.restful_api.interfaces;

import org.springframework.http.ResponseEntity;

import com.car_dealer_web.restful_api.dtos.joins.RoleJoinDTO;
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

public interface IRole {
  ResponseEntity<ApiResponse<PaginationResponse<RoleJoinDTO>>> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<RoleJoinDTO>> findOne(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Role>> save(CreateRoleRequest createRoleRequest, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> update(String id, UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> restore(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> delete(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> forceDelete(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> attachPermissions(String id, AttachPermissionsRequest attachPermissionsRequest,
      HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> detachPermissions(String id, DetachPermissionsRequest detachPermissionsRequest,
      HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> syncPermissions(String id, SyncPermissionsRequest syncPermissionsRequest,
      HttpServletRequest httpServletRequest);
}
