package com.car_dealer_web.restful_api.interfaces;

import com.car_dealer_web.restful_api.payloads.requests.auth.LoginRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RefreshAuthTokenRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RegisterRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
  ApiResponse<Object> createNewUser(RegisterRequest registerRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> signIn(LoginRequest loginRequest, HttpServletRequest httpServletRequest);

  ApiResponse<Object> fetchSelf(HttpServletRequest request);

  ApiResponse<Object> refreshAccessToken(RefreshAuthTokenRequest refreshAuthTokenRequest,
      HttpServletRequest httpServletRequest);
}
