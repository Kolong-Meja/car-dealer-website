package com.car_dealer_web.restful_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.car_dealer_web.restful_api.interfaces.IUser;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.users.UpdateUserRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class UserController {
  private final IUser iUser;

  public UserController(IUser iUser) {
    this.iUser = iUser;
  }

  @GetMapping("/")
  public ResponseEntity<ApiResponse> findAll(@RequestParam(value = "q", required = false) SearchRequest searchRequest,
      @RequestParam(required = false) PaginationRequest paginationRequest) {
    return iUser.findAll(searchRequest, paginationRequest);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> findOne(@PathVariable String id) {
    return iUser.findOne(id);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse> update(@PathVariable String id,
      @Valid @RequestBody(required = false) UpdateUserRequest updateUserRequest,
      HttpServletRequest httpServletRequest) {
    return iUser.update(id, updateUserRequest, httpServletRequest);
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse> restore(@PathVariable String id) {
    return iUser.restore(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> delete(@PathVariable String id) {
    return iUser.delete(id);
  }

  @DeleteMapping("/{id}/force")
  public ResponseEntity<ApiResponse> forceDelete(String id) {
    return iUser.forceDelete(id);
  }
}
