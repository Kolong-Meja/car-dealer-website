package com.car_dealer_web.restful_api.handlers;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.car_dealer_web.restful_api.exceptions.UnauthorizedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final UserDetailsService userDetailsService;
  private final JwtAuthHandler jwtAuthHandler;

  private static final List<String> WHITELIST_API_PATH_PATTERN = List.of(
      "/v1/auth/**");

  public JwtAuthFilter(
      UserDetailsService userDetailsService,
      JwtAuthHandler jwtAuthHandler) {
    this.userDetailsService = userDetailsService;
    this.jwtAuthHandler = jwtAuthHandler;
  }

  private boolean isWhitelisted(String path) {
    return WHITELIST_API_PATH_PATTERN.stream().anyMatch(path::startsWith);
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String apiPath = request.getRequestURI().substring(request.getContextPath().length());

    if (isWhitelisted(apiPath)) {
      filterChain.doFilter(request, response);
      return;
    }

    final String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      throw new BadRequestException("Missing or invalid Authorization header.");
    }

    final String jwt = header.substring(7);
    final String userEmail = jwtAuthHandler.extractUsername(jwt);

    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      var userDetails = userDetailsService.loadUserByUsername(userEmail);

      if (!jwtAuthHandler.isTokenValid(jwt, userDetails)) {
        throw new UnauthorizedException("Invalid or expired refresh token.");
      }

      var token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(token);
    }
    filterChain.doFilter(request, response);
  }
}
