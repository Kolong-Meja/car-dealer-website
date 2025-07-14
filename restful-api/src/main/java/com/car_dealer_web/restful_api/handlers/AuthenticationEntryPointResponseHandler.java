package com.car_dealer_web.restful_api.handlers;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.utils.DateTime;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationEntryPointResponseHandler implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      AuthenticationException exception) throws IOException {
    ApiResponse<Object> response = new ApiResponse<>(
        HttpStatus.UNAUTHORIZED.value(),
        false,
        "Unauthorized access. Please login or provide a valid token.",
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    httpServletResponse.setContentType("application/json");
    httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(response));
  }
}
