package com.car_dealer_web.restful_api.handlers;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.car_dealer_web.restful_api.exceptions.BadGatewayException;
import com.car_dealer_web.restful_api.exceptions.InternalServerErrorException;
import com.car_dealer_web.restful_api.exceptions.MethodNotAllowedException;
import com.car_dealer_web.restful_api.exceptions.RequestTooLargeException;
import com.car_dealer_web.restful_api.exceptions.ResourceNotFoundException;
import com.car_dealer_web.restful_api.exceptions.ServiceUnavailableException;
import com.car_dealer_web.restful_api.exceptions.TooManyRequestsException;
import com.car_dealer_web.restful_api.exceptions.UnauthorizedException;
import com.car_dealer_web.restful_api.exceptions.UnprocessableEntityException;
import com.car_dealer_web.restful_api.exceptions.UnsupportedMediaTypeException;
import com.car_dealer_web.restful_api.payloads.responses.ApiResponse;
import com.car_dealer_web.restful_api.utils.DateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationException(
      final MethodArgumentNotValidException exception, HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final Map<String, String> errors = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (_, b) -> b));
    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.BAD_REQUEST.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), errors);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final Map<String, String> errors = exception.getConstraintViolations().stream()
        .collect(Collectors.toMap(
            v -> v.getPropertyPath().toString(),
            ConstraintViolation::getMessage,
            (_, b) -> b));
    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.BAD_REQUEST.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), errors);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Object>> handleMessageNotReadable(HttpMessageNotReadableException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.BAD_REQUEST.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse<Object>> handleMissingParam(MissingServletRequestParameterException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.BAD_REQUEST.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.UNAUTHORIZED.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.FORBIDDEN.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.NOT_FOUND.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodNotAllowedException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(MethodNotAllowedException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.METHOD_NOT_ALLOWED.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(RequestTooLargeException.class)
  public ResponseEntity<ApiResponse<Object>> handlePayloadTooLarge(RequestTooLargeException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.PAYLOAD_TOO_LARGE.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
  }

  @ExceptionHandler(UnsupportedMediaTypeException.class)
  public ResponseEntity<ApiResponse<Object>> handleUnsupportedMediaType(UnsupportedMediaTypeException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(UnprocessableEntityException.class)
  public ResponseEntity<ApiResponse<Object>> handleUnprocessableEntity(UnprocessableEntityException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.UNPROCESSABLE_ENTITY.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(TooManyRequestsException.class)
  public ResponseEntity<ApiResponse<Object>> handleTooManyRequest(TooManyRequestsException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.TOO_MANY_REQUESTS.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(InternalServerErrorException.class)
  public ResponseEntity<ApiResponse<Object>> handleInternalServerError(InternalServerErrorException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BadGatewayException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadGateway(BadGatewayException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.BAD_GATEWAY.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
  }

  @ExceptionHandler(ServiceUnavailableException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadGateway(ServiceUnavailableException exception,
      HttpServletRequest httpServletRequest) {
    LOG.error(exception.getMessage(), exception);

    final ApiResponse<Object> response = new ApiResponse<Object>(HttpStatus.SERVICE_UNAVAILABLE.value(), false,
        exception.getMessage(),
        DateTime.now(), httpServletRequest.getRequestURI(), Map.of());

    return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
  }
}
