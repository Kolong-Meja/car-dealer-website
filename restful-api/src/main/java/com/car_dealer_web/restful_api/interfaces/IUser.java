package com.car_dealer_web.restful_api.interfaces;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.car_dealer_web.restful_api.dtos.joins.UserJoinDTO;
import com.car_dealer_web.restful_api.models.User;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.users.UpdateUserRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IUser {
  ResponseEntity<ApiResponse<PaginationResponse<UserJoinDTO>>> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<UserJoinDTO>> findOne(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> update(String id, UpdateUserRequest updateUserRequest,
      HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> restore(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> delete(String id, HttpServletRequest httpServletRequest);

  ResponseEntity<ApiResponse<Object>> forceDelete(String id, HttpServletRequest httpServletRequest);

  // FOR AUTH
  Optional<User> findOneByEmail(String email);
}
