package com.car_dealer_web.restful_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.car_dealer_web.restful_api.annotations.RateLimit;
import com.car_dealer_web.restful_api.interfaces.IAuthService;
import com.car_dealer_web.restful_api.payloads.requests.auth.LoginRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RefreshAuthTokenRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RegisterRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
  private final IAuthService iAuthService;

  public AuthController(IAuthService iAuthService) {
    this.iAuthService = iAuthService;
  }

  @RateLimit
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody(required = true) LoginRequest loginRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iAuthService.signIn(loginRequest, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Object>> register(
      @Valid @RequestBody(required = true) RegisterRequest registerRequest, HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iAuthService.createNewUser(registerRequest, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<Object>> me(HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iAuthService.fetchSelf(httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }

  @RateLimit
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<Object>> refresh(
      @Valid @RequestBody(required = true) RefreshAuthTokenRequest refreshAuthTokenRequest,
      HttpServletRequest httpServletRequest) {
    ApiResponse<Object> result = iAuthService.refreshAccessToken(refreshAuthTokenRequest, httpServletRequest);

    return ResponseEntity.status(result.status()).body(result);
  }
}
