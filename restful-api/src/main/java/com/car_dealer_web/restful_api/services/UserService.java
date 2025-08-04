package com.car_dealer_web.restful_api.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.car_dealer_web.restful_api.dtos.users.UserJoinDTO;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.interfaces.IUser;
import com.car_dealer_web.restful_api.interfaces.IUserService;
import com.car_dealer_web.restful_api.payloads.requests.PaginationRequest;
import com.car_dealer_web.restful_api.payloads.requests.SearchRequest;
import com.car_dealer_web.restful_api.payloads.requests.users.UpdateUserRequest;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.payloads.responses.PaginationResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService implements IUserService {
  private IUser iUser;

  private final static Logger LOG = LoggerFactory.getLogger(RoleService.class);

  public UserService(IUser iUser) {
    this.iUser = iUser;
  }

  @Override
  public ApiResponse<PaginationResponse<UserJoinDTO>> getAllUsers(SearchRequest searchRequest,
      PaginationRequest paginationRequest, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("%s: Processing request to get all users data", getClass().getSimpleName()));

    try {
      PaginationResponse<UserJoinDTO> resource = iUser.findAll(searchRequest, paginationRequest);

      ApiResponse<PaginationResponse<UserJoinDTO>> response = new ApiResponse<>(
          HttpStatus.OK.value(),
          true,
          "Succesfully fetch users.",
          DateTime.now(),
          httpServletRequest.getRequestURI(),
          resource);

      LOG.info(response.message());

      return response;
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ApiResponse<UserJoinDTO> getOneUser(String id, HttpServletRequest httpServletRequest) {
    LOG.info(String.format("%s: Processing request to get one user data with ID %s", getClass().getSimpleName(), id));

    try {
      UserJoinDTO resource = iUser.findOne(id);

      ApiResponse<UserJoinDTO> response = new ApiResponse<>(HttpStatus.OK.value(), true,
          String.format("Successfully fetch user with ID %s", id),
          DateTime.now(),
          httpServletRequest.getRequestURI(),
          resource);

      LOG.info(response.message());

      return response;
    } catch (NoResultException e) {
      LOG.error(e.getMessage());

      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ApiResponse<Object> modifyUser(String id, UpdateUserRequest updateUserRequest,
      HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to update one user data with ID %s.", getClass().getSimpleName(), id));

    int updatedRows = iUser.update(id, updateUserRequest, httpServletRequest);

    if (updatedRows != 1) {
      LOG.error(String.format("User with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("User with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully update user entity with ID %s.", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> restoreUser(String id, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to restore one user data with ID %s.", getClass().getSimpleName(), id));

    int restoredRows = iUser.restore(id, httpServletRequest);

    if (restoredRows != 1) {
      LOG.error(String.format("User with ID %s not found or is not deleted.", id));

      throw new ResourceNotFoundException(String.format("User with ID %s not found or is not deleted.", id));
    }

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully restore user entity with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> deleteUser(String id, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to delete one user data with ID %s.", getClass().getSimpleName(), id));

    int deletedRows = iUser.delete(id, httpServletRequest);

    if (deletedRows != 1) {
      LOG.error(String.format("User with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("User with ID %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully soft delete user with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return response;
  }

  @Override
  public ApiResponse<Object> forceDeleteUser(String id, HttpServletRequest httpServletRequest) {
    LOG.info(
        String.format("%s: Processing request to force delete one user data with ID %s.", getClass().getSimpleName(),
            id));

    int forceDeletedRows = iUser.forceDelete(id, httpServletRequest);

    if (forceDeletedRows != 1) {
      LOG.error(String.format("User with ID %s not found.", id));

      throw new ResourceNotFoundException(String.format("User with id %s not found.", id));
    }

    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), true,
        String.format("Successfully force delete user entity with ID %s", id),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    LOG.info(response.message());

    return response;
  }

}
