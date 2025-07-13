package com.car_dealer_web.restful_api.interfaces;

import org.springframework.http.ResponseEntity;

import com.car_dealer_web.restful_api.payloads.requests.auth.LoginRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RefreshAuthTokenRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RegisterRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuth {
  ResponseEntity<ApiResponse<Object>> register(RegisterRequest request);

  ResponseEntity<ApiResponse<Object>> login(LoginRequest request);

  ResponseEntity<ApiResponse<Object>> me(HttpServletRequest request);

  ResponseEntity<ApiResponse<Object>> refresh(HttpServletRequest httpServletRequest,
      RefreshAuthTokenRequest refreshAuthTokenRequest);
}
