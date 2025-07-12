package com.car_dealer_web.restful_api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.car_dealer_web.restful_api.interfaces.IUser;

@Configuration
public class AppConfig {
  private final IUser iUser;

  public AppConfig(IUser iUser) {
    this.iUser = iUser;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return email -> iUser.findOneByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user is not found."));
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    var authProvider = new DaoAuthenticationProvider(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
