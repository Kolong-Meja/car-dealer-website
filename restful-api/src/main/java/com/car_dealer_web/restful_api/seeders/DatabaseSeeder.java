package com.car_dealer_web.restful_api.seeders;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {
  private final RoleSeeder roleSeeder;

  public DatabaseSeeder(
      RoleSeeder roleSeeder) {
    this.roleSeeder = roleSeeder;
  }

  @Override
  public void run(String... args) throws Exception {
    roleSeeder.loadRoleData();
  }
}
