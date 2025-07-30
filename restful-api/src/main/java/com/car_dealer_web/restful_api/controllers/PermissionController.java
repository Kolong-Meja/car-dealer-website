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
import com.car_dealer_web.restful_api.dtos.permissions.PermissionJoinDTO;
import com.car_dealer_web.restful_api.dtos.permissions.PermissionWithRolesDTO;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/permissions")
public class PermissionController {
  private final IPermissionService iPermissionService;

  public PermissionController(IPermissionService iPermissionService) {
    this.iPermissionService = iPermissionService;
  }

  @RateLimit
  @GetMapping("/")
  public ResponseEntity<ApiResponse<PaginationResponse<PermissionJoinDTO>>> findAll(
      @RequestParam(value = "q", required = false) SearchRequest searchRequest,
      @RequestParam(required = false) PaginationRequest paginationRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<PaginationResponse<PermissionJoinDTO>> result = iPermissionService.getAllPermissions(searchRequest,
        paginationRequest, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PermissionJoinDTO>> findOne(
      @PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<PermissionJoinDTO> result = iPermissionService.getOnePermission(id,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @PostMapping("/")
  public ResponseEntity<ApiResponse<Permission>> save(
      @Valid @RequestBody(required = true) CreatePermissionRequest createPermissionRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Permission> result = iPermissionService.createNewPermission(
        createPermissionRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable String id,
      @Valid @RequestBody(required = false) UpdatePermissionRequest updatePermissionRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iPermissionService.modifyPermission(id, updatePermissionRequest, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse<Object>> restore(@PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iPermissionService.restorePermission(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iPermissionService.deletePermission(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @DeleteMapping("/{id}/force")
  public ResponseEntity<ApiResponse<Object>> forceDelete(
      @PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iPermissionService.forceDeletePermission(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @GetMapping("/{id}/roles")
  public ResponseEntity<ApiResponse<PermissionWithRolesDTO>> fetchRoles(
      @PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<PermissionWithRolesDTO> result = iPermissionService.getOnePermissionWithRoles(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PostMapping("/{id}/roles")
  public ResponseEntity<ApiResponse<Object>> attachRoles(
      @PathVariable String id,
      @Valid @RequestBody(required = true) AttachRolesRequest attachRolesRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iPermissionService.attachOnePermissionWithRoles(id, attachRolesRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PutMapping("/{id}/roles/detach")
  public ResponseEntity<ApiResponse<Object>> detachRoles(
      @PathVariable String id,
      @Valid @RequestBody(required = true) DetachRolesRequest detachRolesRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iPermissionService.detachOnePermissionWithRoles(id, detachRolesRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PutMapping("/{id}/roles")
  public ResponseEntity<ApiResponse<Object>> syncRoles(
      @PathVariable String id,
      @Valid @RequestBody(required = true) SyncRolesRequest syncRolesRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iPermissionService.syncOnePermissionWithRoles(id, syncRolesRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }
}
