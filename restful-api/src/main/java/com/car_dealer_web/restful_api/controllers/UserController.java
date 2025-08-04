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

import com.car_dealer_web.restful_api.annotations.RateLimit;
import com.car_dealer_web.restful_api.dtos.users.UserJoinDTO;
import com.car_dealer_web.restful_api.interfaces.IUserService;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.users.UpdateUserRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class UserController {
  private final IUserService iUserService;

  public UserController(IUserService iUserService) {
    this.iUserService = iUserService;
  }

  @RateLimit
  @GetMapping("/")
  public ResponseEntity<ApiResponse<PaginationResponse<UserJoinDTO>>> findAll(
      @RequestParam(value = "q", required = false) SearchRequest searchRequest,
      @RequestParam(required = false) PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    ApiResponse<PaginationResponse<UserJoinDTO>> result = iUserService.getAllUsers(searchRequest, paginationRequest,
        httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<UserJoinDTO>> findOne(@PathVariable String id,
      HttpServletRequest httpServletRequest) {
    ApiResponse<UserJoinDTO> result = iUserService.getOneUser(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(@PathVariable String id,
      @Valid @RequestBody(required = false) UpdateUserRequest updateUserRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iUserService.modifyUser(id, updateUserRequest, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse<Object>> restore(@PathVariable String id, HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iUserService.restoreUser(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id, HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iUserService.deleteUser(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @DeleteMapping("/{id}/force")
  public ResponseEntity<ApiResponse<Object>> forceDelete(String id, HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iUserService.forceDeleteUser(id, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }
}
