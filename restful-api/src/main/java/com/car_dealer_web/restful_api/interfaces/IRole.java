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
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IRole {
  PaginationResponse<RoleJoinDTO> findAll(
      SearchRequest searchRequest,
      PaginationRequest paginationRequest);

  RoleJoinDTO findOne(String id);

  Role save(CreateRoleRequest createRoleRequest);

  int update(
      String id,
      UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest);

  int restore(
      String id,
      HttpServletRequest httpServletRequest);

  int delete(
      String id,
      HttpServletRequest httpServletRequest);

  int forceDelete(String id);

  RoleWithPermissionsDTO fetchPermissions(String id);

  void attachPermissions(
      String id,
      AttachPermissionsRequest attachPermissionsRequest);

  void detachPermissions(
      String id,
      DetachPermissionsRequest detachPermissionsRequest);

  void syncPermissions(
      String id,
      SyncPermissionsRequest syncPermissionsRequest);
}
