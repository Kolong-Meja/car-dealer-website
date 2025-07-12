package com.car_dealer_web.restful_api.interfaces;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.car_dealer_web.restful_api.models.User;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.users.UpdateUserRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IUser {
  ResponseEntity<ApiResponse> findAll(SearchRequest searchRequest, PaginationRequest paginationRequest);

  ResponseEntity<ApiResponse> findOne(String id);

  ResponseEntity<ApiResponse> update(String id, UpdateUserRequest updateUserRequest,
      HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse> restore(String id);

  ResponseEntity<ApiResponse> delete(String id);

  ResponseEntity<ApiResponse> forceDelete(String id);

  // FOR AUTH
  Optional<User> findOneByEmail(String email);
}
