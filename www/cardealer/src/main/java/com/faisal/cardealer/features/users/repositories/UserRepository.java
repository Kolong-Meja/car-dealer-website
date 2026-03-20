package com.faisal.cardealer.features.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.faisal.cardealer.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
  Optional<User> findByActiveEmail(@Param("email") String email);

  @Query("SELECT u FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
  Optional<User> findActiveByUsername(@Param("username") String username);

  @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
  boolean existsActiveByUsername(@Param("username") String username);

  @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
  boolean existsActiveByEmail(@Param("email") String email);
}
