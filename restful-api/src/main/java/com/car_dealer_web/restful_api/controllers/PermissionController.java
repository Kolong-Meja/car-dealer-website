package com.car_dealer_web.restful_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.car_dealer_web.restful_api.annotations.RateLimit;
import com.car_dealer_web.restful_api.dtos.joins.PermissionJoinDTO;
import com.car_dealer_web.restful_api.interfaces.IPermission;
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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/permissions")
public class PermissionController {
  private final IPermission iPermission;

  public PermissionController(IPermission iPermission) {
    this.iPermission = iPermission;
  }

  @GetMapping("/")
  @RateLimit
  public ResponseEntity<ApiResponse<PaginationResponse<PermissionJoinDTO>>> findAll(
      @RequestParam(value = "q", required = false) SearchRequest searchRequest,
      @RequestParam(required = false) PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    return iPermission.findAll(searchRequest, paginationRequest, httpServletRequest);
  }

  @GetMapping("/{id}")
  @RateLimit
  public ResponseEntity<ApiResponse<PermissionJoinDTO>> findOne(@PathVariable String id,
      HttpServletRequest httpServletRequest) {
    return iPermission.findOne(id, httpServletRequest);
  }

  @PostMapping("/")
  @RateLimit
  public ResponseEntity<ApiResponse<Permission>> save(
      @Valid @RequestBody(required = true) CreatePermissionRequest createPermissionRequest,
      HttpServletRequest httpServletRequest) {
    return iPermission.save(createPermissionRequest, httpServletRequest);
  }

  @PatchMapping("/{id}")
  @RateLimit
  public ResponseEntity<ApiResponse<Object>> update(@PathVariable String id,
      @Valid @RequestBody(required = false) UpdatePermissionRequest updatePermissionRequest,
      HttpServletRequest httpServletRequest) {
    return iPermission.update(id, updatePermissionRequest, httpServletRequest);
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse<Object>> restore(@PathVariable String id, HttpServletRequest httpServletRequest) {
    return iPermission.restore(id, httpServletRequest);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, HttpServletRequest httpServletRequest) {
    return iPermission.delete(id, httpServletRequest);
  }

  @DeleteMapping("/{id}/force")
  public ResponseEntity<ApiResponse<Object>> forceDelete(@PathVariable String id,
      HttpServletRequest httpServletRequest) {
    return iPermission.forceDelete(id, httpServletRequest);
  }

  @GetMapping("/{id}/roles")
  public void fetchRoles() {
  }

  @PostMapping("/{id}/roles")
  public ResponseEntity<ApiResponse<Object>> attachRoles(@PathVariable String id,
      @Valid @RequestBody(required = true) AttachRolesRequest attachRolesRequest,
      HttpServletRequest httpServletRequest) {
    return iPermission.attachRoles(id, attachRolesRequest, httpServletRequest);
  }

  @PutMapping("/{id}/roles/detach")
  public ResponseEntity<ApiResponse<Object>> detachRoles(@PathVariable String id,
      @Valid @RequestBody(required = true) DetachRolesRequest detachRolesRequest,
      HttpServletRequest httpServletRequest) {
    return iPermission.detachRoles(id, detachRolesRequest, httpServletRequest);
  }

  @PutMapping("/{id}/roles")
  public ResponseEntity<ApiResponse<Object>> syncRoles(@PathVariable String id,
      @Valid @RequestBody(required = true) SyncRolesRequest syncRolesRequest,
      HttpServletRequest httpServletRequest) {
    return iPermission.syncRoles(id, syncRolesRequest, httpServletRequest);
  }
}
