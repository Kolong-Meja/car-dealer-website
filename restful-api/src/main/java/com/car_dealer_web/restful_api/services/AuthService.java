package com.car_dealer_web.restful_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.car_dealer_web.restful_api.interfaces.IAuth;
import com.car_dealer_web.restful_api.interfaces.IAuthService;
import com.car_dealer_web.restful_api.payloads.requests.auth.LoginRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RefreshAuthTokenRequest;
import com.car_dealer_web.restful_api.payloads.requests.auth.RegisterRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService implements IAuthService {
  private final IAuth iAuth;

  private final static Logger LOG = LoggerFactory.getLogger(AuthService.class);

  public AuthService(IAuth iAuth) {
    this.iAuth = iAuth;
  }

  @Override
  public ApiResponse<Object> createNewUser(RegisterRequest registerRequest, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("%s: Registering new user.", getClass().getSimpleName()));

    var resource = iAuth.register(registerRequest);

    // SETUP THE API (JSON) RESPONSE.
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CREATED.value(), true,
        "Successfully registering new user.",
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        resource);

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> signIn(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("%s: Doing login as %s.", getClass().getSimpleName(), loginRequest.email()));

    var resource = iAuth.login(loginRequest, httpServletRequest);

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Login is success. Welcome back %s", loginRequest.email()),
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        resource);

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> fetchSelf(HttpServletRequest request) {
    LOG.info(String.format("%s: Checking user data by passing JWT.", getClass().getSimpleName()));

    var resource = iAuth.me(request);

    // SETUP THE API (JSON) RESPONSE.
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true, "Successfully fetch personal data.",
        DateTime.now(),
        request.getRequestURI(),
        resource);

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> refreshAccessToken(RefreshAuthTokenRequest refreshAuthTokenRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(String.format("%s: Refreshing auth or access token.", getClass().getSimpleName()));

    var resource = iAuth.refresh(refreshAuthTokenRequest, httpServletRequest);

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        "Successfully refresh auth or access token.",
        DateTime.now(), httpServletRequest.getRequestURI(), resource);

    LOG.info(response.message());

    return response;
  }
}
