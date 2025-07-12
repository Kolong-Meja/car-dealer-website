package com.car_dealer_web.restful_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.car_dealer_web.restful_api.interfaces.IRole;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.CreateRoleRequest;
import com.car_dealer_web.restful_api.payloads.requests.roles.UpdateRoleRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

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
  public ResponseEntity<ApiResponse> findAll(@RequestParam(value = "q", required = false) SearchRequest searchRequest,
      @RequestParam(required = false) PaginationRequest paginationRequest) {
    return iRole.findAll(searchRequest, paginationRequest);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> findOne(@PathVariable String id) {
    return iRole.findOne(id);
  }

  @PostMapping("/")
  public ResponseEntity<ApiResponse> save(@Valid @RequestBody(required = true) CreateRoleRequest request) {
    return iRole.save(request);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse> update(@PathVariable String id,
      @Valid @RequestBody(required = false) UpdateRoleRequest updateRoleRequest,
      HttpServletRequest httpServletRequest) {
    return iRole.update(id, updateRoleRequest, httpServletRequest);
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse> restore(@PathVariable String id) {
    return iRole.restore(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> delete(@PathVariable String id) {
    return iRole.delete(id);
  }

  @DeleteMapping("/{id}/force")
  public ResponseEntity<ApiResponse> forceDelete(@PathVariable String id) {
    return iRole.forceDelete(id);
  }
}
