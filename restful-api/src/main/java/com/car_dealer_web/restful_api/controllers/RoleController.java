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
import com.car_dealer_web.restful_api.dtos.joins.RoleJoinDTO;
import com.car_dealer_web.restful_api.interfaces.IRole;
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
  private final IRole iRole;

  public RoleController(IRole iRole) {
    this.iRole = iRole;
  }

  @GetMapping("/")
  @RateLimit
  public ResponseEntity<ApiResponse<PaginationResponse<RoleJoinDTO>>> findAll(
      @RequestParam(value = "q", required = false) SearchRequest searchRequest,
      @RequestParam(required = false) PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    return iRole.findAll(searchRequest, paginationRequest, httpServletRequest);
  }

  @GetMapping("/{id}")
  @RateLimit
  public ResponseEntity<ApiResponse<RoleJoinDTO>> findOne(@PathVariable String id,
      HttpServletRequest httpServletRequest) {
    return iRole.findOne(id, httpServletRequest);
  }

  @PostMapping("/")
  @RateLimit
  public ResponseEntity<ApiResponse<Role>> save(
      @Valid @RequestBody(required = true) CreateRoleRequest createRoleRequest, HttpServletRequest httpServletRequest) {
    return iRole.save(createRoleRequest, httpServletRequest);
  }

  @PatchMapping("/{id}")
  @RateLimit
  public ResponseEntity<ApiResponse<Object>> update(@PathVariable String id,
      @Valid @RequestBody(required = false) UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest) {
    return iRole.update(id, updateRoleRequest, httpServletRequest);
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse<Object>> restore(@PathVariable String id, HttpServletRequest httpServletRequest) {
    return iRole.restore(id, httpServletRequest);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, HttpServletRequest httpServletRequest) {
    return iRole.delete(id, httpServletRequest);
  }

  @DeleteMapping("/{id}/force")
  public ResponseEntity<ApiResponse<Object>> forceDelete(@PathVariable String id,
      HttpServletRequest httpServletRequest) {
    return iRole.forceDelete(id, httpServletRequest);
  }

  @GetMapping("/{id}/permissions")
  public void fetchPermissions() {
  }

  @PostMapping("/{id}/permissions")
  public ResponseEntity<ApiResponse<Object>> attachPermissions(@PathVariable String id,
      @Valid @RequestBody(required = true) AttachPermissionsRequest attachPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    return iRole.attachPermissions(id, attachPermissionsRequest, httpServletRequest);
  }

  @PutMapping("/{id}/permissions/detach")
  public ResponseEntity<ApiResponse<Object>> detachPermissions(@PathVariable String id,
      @Valid @RequestBody(required = true) DetachPermissionsRequest detachPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    return iRole.detachPermissions(id, detachPermissionsRequest, httpServletRequest);
  }

  @PutMapping("/{id}/permissions")
  public ResponseEntity<ApiResponse<Object>> syncPermissions(@PathVariable String id,
      @Valid @RequestBody(required = true) SyncPermissionsRequest syncPermissionsRequest,
      HttpServletRequest httpServletRequest) {
    return iRole.syncPermissions(id, syncPermissionsRequest, httpServletRequest);
  }
}
