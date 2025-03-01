package com.tms.app.services.token.Impl;

import com.tms.app.entities.user.User;
import com.tms.app.entities.userSession.UserSession;
import com.tms.app.enums.TokenType;
import com.tms.app.repositories.token.UserSessionRepository;
import com.tms.app.services.token.UserSessionService;
import com.tms.app.utils.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    private final AppLogger log = new AppLogger(UserSessionServiceImpl.class);

    @Override
    public void saveUserSession(String token, User user) {

        log.info("Saving User Session");

        log.info("Deleting All User Sessions");
        deleteAllSessions(user);

        UserSession userSession = new UserSession();
        userSession.setIsActive(true);
        userSession.setToken(token);
        userSession.setUser(user);
        userSession.setTokenType(TokenType.BEARER);
        this.userSessionRepository.save(userSession);

        log.info("User Session Saved Successfully");
    }

    @Override
    public Optional<UserSession> findSessionByToken(String jwtToken) {

        return this.userSessionRepository.findSessionByToken(jwtToken);
    }

    @Override
    public Optional<UserSession> findSessionByUser(User user) {

        return this.userSessionRepository.findSessionByUser(user.getId());
    }

    @Override
    public List<UserSession> findAllSessions(User user) {

        return this.userSessionRepository.findAllSessions(user.getId());
    }

    @Override
    public void deleteSession(String token) {

        Optional<UserSession> optionalUserSession = findSessionByToken(token);
        optionalUserSession.ifPresent(this.userSessionRepository::delete);
        log.info("User Session Deleted Successfully");
    }

    @Override
    public void deleteAllSessions(User user) {

        List<UserSession> userSessions = this.userSessionRepository.findAllSessions(user.getId());
        if (userSessions != null && !userSessions.isEmpty()) {
            this.userSessionRepository.deleteAll(userSessions);
        }
        log.info("User Sessions Deleted Successfully");
    }
}