package com.car_dealer_web.restful_api.interfaces;

import org.springframework.http.ResponseEntity;

import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.CreateRoleRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.UpdateRoleRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IRole {
    ResponseEntity<ApiResponse> findAll(SearchRequest searchRequest, PaginationRequest paginationRequest);

  ResponseEntity<ApiResponse> findOne(String id);

  ResponseEntity<ApiResponse> save(CreateRoleRequest request);

  ResponseEntity<ApiResponse> update(String id, UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse> restore(String id);

  ResponseEntity<ApiResponse> delete(String id);

  ResponseEntity<ApiResponse> forceDelete(String id);
}
