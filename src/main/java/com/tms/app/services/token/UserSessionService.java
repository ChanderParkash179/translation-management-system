package com.tms.app.services.token;

import com.tms.app.entities.user.User;
import com.tms.app.entities.userSession.UserSession;

import java.util.List;
import java.util.Optional;

public interface UserSessionService {

    void saveUserSession(String token, User user);

    Optional<UserSession> findSessionByToken(String jwtToken);

    Optional<UserSession> findSessionByUser(User user);

    List<UserSession> findAllSessions(User user);

    void deleteSession(String token);

    void deleteAllSessions(User user);
}