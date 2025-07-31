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
import com.car_dealer_web.restful_api.dtos.roles.RoleJoinDTO;
import com.car_dealer_web.restful_api.dtos.roles.RoleWithPermissionsDTO;
import com.car_dealer_web.restful_api.interfaces.IRoleService;
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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/roles")
public class RoleController {
  private final IRoleService iRoleService;

  public RoleController(IRoleService iRoleService) {
    this.iRoleService = iRoleService;
  }

  @RateLimit
  @GetMapping("/")
  public ResponseEntity<ApiResponse<PaginationResponse<RoleJoinDTO>>> findAll(
      @RequestParam(value = "q", required = false) SearchRequest searchRequest,
      @RequestParam(required = false) PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    ApiResponse<PaginationResponse<RoleJoinDTO>> result = iRoleService.getAllRoles(searchRequest, paginationRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<RoleJoinDTO>> findOne(
      @PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<RoleJoinDTO> result = iRoleService.getOneRole(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @PostMapping("/")
  public ResponseEntity<ApiResponse<Role>> save(
      @Valid @RequestBody(required = true) CreateRoleRequest createRoleRequest, HttpServletRequest httpServletRequest) {
    ApiResponse<Role> result = iRoleService.createNewRole(createRoleRequest, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable String id,
      @Valid @RequestBody(required = false) UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iRoleService.modifyRole(id, updateRoleRequest, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse<Object>> restore(
      @PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iRoleService.restoreRole(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(
      @PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iRoleService.deleteRole(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @DeleteMapping("/{id}/force")
  public ResponseEntity<ApiResponse<Object>> forceDelete(
      @PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iRoleService.forceDeleteRole(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @GetMapping("/{id}/permissions")
  public ResponseEntity<ApiResponse<RoleWithPermissionsDTO>> fetchPermissions(
      @PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<RoleWithPermissionsDTO> result = iRoleService.getOneRoleWithPermissions(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PostMapping("/{id}/permissions")
  public ResponseEntity<ApiResponse<Object>> attachPermissions(
      @PathVariable String id,
      @Valid @RequestBody(required = true) AttachPermissionsRequest attachPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iRoleService.attachOneRoleWithPermissions(id, attachPermissionsRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PutMapping("/{id}/permissions/detach")
  public ResponseEntity<ApiResponse<Object>> detachPermissions(
      @PathVariable String id,
      @Valid @RequestBody(required = true) DetachPermissionsRequest detachPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iRoleService.detachOneRoleWithPermissions(id, detachPermissionsRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PutMapping("/{id}/permissions")
  public ResponseEntity<ApiResponse<Object>> syncPermissions(
      @PathVariable String id,
      @Valid @RequestBody(required = true) SyncPermissionsRequest syncPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iRoleService.syncOneRoleWithPermissions(id, syncPermissionsRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }
}
