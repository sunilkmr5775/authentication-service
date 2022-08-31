package com.userservice.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.userservice.app.models.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
//  Optional<Users> findByUsername(String username);
  Users findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
  
  @Query("SELECT u FROM Users u WHERE u.verificationCode = ?1")
  public Users findByVerificationCode(String code);
}
