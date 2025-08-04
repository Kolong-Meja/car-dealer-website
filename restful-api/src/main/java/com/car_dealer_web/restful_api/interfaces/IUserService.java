package com.car_dealer_web.restful_api.interfaces;

import com.car_dealer_web.restful_api.dtos.users.UserJoinDTO;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.users.UpdateUserRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {
  ApiResponse<PaginationResponse<UserJoinDTO>> getAllUsers(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest);

  ApiResponse<UserJoinDTO> getOneUser(String id, HttpServletRequest httpServletRequest);

  ApiResponse<Object> modifyUser(String id, UpdateUserRequest updateUserRequest,
      HttpServletRequest httpServletRequest);

  ApiResponse<Object> restoreUser(String id, HttpServletRequest httpServletRequest);

  ApiResponse<Object> deleteUser(String id, HttpServletRequest httpServletRequest);

  ApiResponse<Object> forceDeleteUser(String id, HttpServletRequest httpServletRequest);
}
