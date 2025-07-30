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
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IPermission {
  PaginationResponse<PermissionJoinDTO> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest);

  PermissionJoinDTO findOne(String id);

  Permission save(CreatePermissionRequest createPermissionRequest);

  int update(String id, UpdatePermissionRequest updatePermissionRequest, HttpServletRequest httpServletRequest);

  int restore(String id, HttpServletRequest httpServletRequest);

  int delete(String id, HttpServletRequest httpServletRequest);

  int forceDelete(String id);

  PermissionWithRolesDTO fetchRoles(String id);

  void attachRoles(String id, AttachRolesRequest attachRolesRequest);

  void detachRoles(String id, DetachRolesRequest detachRolesRequest);

  void syncRoles(String id, SyncRolesRequest syncRolesRequest);
}
