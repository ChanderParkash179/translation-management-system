package com.tms.app.repositories.token;

import com.tms.app.entities.userSession.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findSessionByToken(String token);

    @Query("SELECT s FROM UserSession s " +
            "JOIN User u on s.user.id = u.id " +
            "WHERE u.id = :userId AND s.isActive = TRUE")
    Optional<UserSession> findSessionByUser(UUID userId);

    @Query("SELECT s FROM UserSession s " +
            "JOIN User u on s.user.id = u.id " +
            "WHERE u.id = :userId AND s.isActive = TRUE")
    List<UserSession> findAllSessions(UUID userId);
}