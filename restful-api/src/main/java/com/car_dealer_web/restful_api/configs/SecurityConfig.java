package com.car_dealer_web.restful_api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.car_dealer_web.restful_api.handlers.AccessDeniedResponseHandler;
import com.car_dealer_web.restful_api.handlers.AuthenticationEntryPointResponseHandler;
import com.car_dealer_web.restful_api.handlers.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private final JwtAuthFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final AccessDeniedResponseHandler accessDeniedResponseHandler;
  private final AuthenticationEntryPointResponseHandler authenticationEntryPointResponseHandler;

  public SecurityConfig(
      JwtAuthFilter jwtAuthFilter,
      AuthenticationProvider authenticationProvider,
      AccessDeniedResponseHandler accessDeniedResponseHandler,
      AuthenticationEntryPointResponseHandler authenticationEntryPointResponseHandler) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.authenticationProvider = authenticationProvider;
    this.accessDeniedResponseHandler = accessDeniedResponseHandler;
    this.authenticationEntryPointResponseHandler = authenticationEntryPointResponseHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity.authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/", "/v1/auth/**")
        .permitAll()
        .requestMatchers("/v1/admin/**")
        .hasAnyRole("admin", "super admin")
        .anyRequest()
        .authenticated())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
