package com.tms.app.repositories.user;

import com.tms.app.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE lower(u.email) = lower(:email) AND u.isActive = TRUE")
    Optional<User> findActiveUserByEmail(String email);

    @Query("select u from User u WHERE lower(u.email) = lower(:username) OR lower(u.username) = lower(:username) AND u.isActive = TRUE")
    Optional<User> findActiveUserByEmailOrUsername(String username);
}