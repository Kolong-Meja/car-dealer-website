package com.car_dealer_web.restful_api.interfaces;

import com.car_dealer_web.restful_api.payloads.requests.auth.LoginRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RefreshAuthTokenRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuth {
  Object register(RegisterRequest registerRequest);

  Object login(LoginRequest loginRequest, HttpServletRequest httpServletRequest);

  Object me(HttpServletRequest request);

  Object refresh(RefreshAuthTokenRequest refreshAuthTokenRequest,
      HttpServletRequest httpServletRequest);
}
