package com.car_dealer_web.restful_api.handlers;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.utils.DateTime;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedResponseHandler implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      AccessDeniedException exception) throws IOException {
    ApiResponse<Object> response = new ApiResponse<>(
        HttpStatus.FORBIDDEN.value(),
        false,
        "Access denied. You do not have permission to access this resource.",
        DateTime.now(),
        httpServletRequest.getRequestURI(),
        Map.of());

    httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
    httpServletResponse.setContentType("application/json");
    httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(response));
  }
}
