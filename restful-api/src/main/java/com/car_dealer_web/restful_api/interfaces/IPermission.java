package com.car_dealer_web.restful_api.interfaces;

import org.springframework.http.ResponseEntity;

import com.car_dealer_web.restful_api.dtos.joins.PermissionJoinDTO;
import com.car_dealer_web.restful_api.models.Permission;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.CreatePermissionRequest;
import com.car_dealer_web.restful_api.payloads.requests.permissions.UpdatePermissionRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IPermission {
  ResponseEntity<ApiResponse<PaginationResponse<PermissionJoinDTO>>> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<PermissionJoinDTO>> findOne(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Permission>> save(CreatePermissionRequest createPermissionRequest,
      HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> update(String id, UpdatePermissionRequest updatePermissionRequest,
      HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> restore(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> delete(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> forceDelete(String id, HttpServletRequest httpServletRequest);
}
