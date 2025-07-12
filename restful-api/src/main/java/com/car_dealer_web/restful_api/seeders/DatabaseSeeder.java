package com.car_dealer_web.restful_api.seeders;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {
  private final RoleSeeder roleSeeder;
  private final UserSeeder userSeeder;

  public DatabaseSeeder(
      RoleSeeder roleSeeder,
      UserSeeder userSeeder) {
    this.roleSeeder = roleSeeder;
    this.userSeeder = userSeeder;
  }

  @Override
  public void run(String... args) throws Exception {
    roleSeeder.loadRoleData();
    userSeeder.loadUserData();
  }
}
