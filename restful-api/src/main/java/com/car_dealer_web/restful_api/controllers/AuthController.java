package com.car_dealer_web.restful_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.car_dealer_web.restful_api.interfaces.IAuth;
import com.car_dealer_web.restful_api.payloads.requests.auth.LoginRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RefreshAuthTokenRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RegisterRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
  private final IAuth iAuth;

  public AuthController(IAuth iAuth) {
    this.iAuth = iAuth;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse> login(@Valid @RequestBody(required = true) LoginRequest loginRequest) {
    return iAuth.login(loginRequest);
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse> register(@Valid @RequestBody(required = true) RegisterRequest registerRequest) {
    return iAuth.register(registerRequest);
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse> me(HttpServletRequest httpServletRequest) {
    return iAuth.me(httpServletRequest);
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse> refresh(HttpServletRequest httpServletRequest,
      @Valid @RequestBody(required = true) RefreshAuthTokenRequest refreshAuthTokenRequest) {
    return iAuth.refresh(httpServletRequest, refreshAuthTokenRequest);
  }
}
