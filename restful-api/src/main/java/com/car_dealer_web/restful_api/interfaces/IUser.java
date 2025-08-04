package com.car_dealer_web.restful_api.interfaces;

import java.util.Optional;

import com.car_dealer_web.restful_api.dtos.users.UserJoinDTO;
import com.car_dealer_web.restful_api.models.User;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.users.UpdateUserRequest;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IUser {
  PaginationResponse<UserJoinDTO> findAll(SearchRequest searchRequest,
      PaginationRequest paginationRequest);

  UserJoinDTO findOne(String id);

  int update(String id, UpdateUserRequest updateUserRequest,
      HttpServletRequest httpServletRequest);

  int restore(String id, HttpServletRequest httpServletRequest);

  int delete(String id, HttpServletRequest httpServletRequest);

  int forceDelete(String id, HttpServletRequest httpServletRequest);

  // FOR AUTH
  Optional<User> findOneByEmail(String email);
}
