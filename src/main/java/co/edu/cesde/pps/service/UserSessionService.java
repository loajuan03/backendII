package co.edu.cesde.pps.service;

import co.edu.cesde.pps.config.AppConfig;
import co.edu.cesde.pps.exception.AuthenticationException;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.repository.UserSessionRepository;
import co.edu.cesde.pps.security.SessionTokenGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Gestion de sesiones opacas por token.
 */
@Service
@Transactional(readOnly = true)
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final SessionTokenGenerator sessionTokenGenerator;

    public UserSessionService(UserSessionRepository repo, SessionTokenGenerator gen) {
        this.userSessionRepository = repo;
        this.sessionTokenGenerator = gen;
    }

    @Transactional
    public UserSession createGuestSession() {
        LocalDateTime now = LocalDateTime.now();
        return userSessionRepository.save(UserSession.builder()
                .user(null)
                .sessionToken(generateUniqueToken())
                .createdAt(now)
                .expiresAt(now.plusHours(AppConfig.getGuestSessionTimeoutHours()))
                .build());
    }

    @Transactional
    public UserSession createAuthenticatedSession(User user) {
        LocalDateTime now = LocalDateTime.now();
        return userSessionRepository.save(UserSession.builder()
                .user(user)
                .sessionToken(generateUniqueToken())
                .createdAt(now)
                .expiresAt(now.plusHours(AppConfig.getUserSessionTimeoutHours()))
                .build());
    }

    public UserSession requireActiveSession(String token) {
        UserSession session = userSessionRepository.findBySessionToken(token)
                .orElseThrow(() -> new EntityNotFoundException("UserSession", token));
        if (session.isExpired()) {
            throw new AuthenticationException("Session token has expired");
        }
        return session;
    }

    public User requireAuthenticatedUser(String token) {
        UserSession session = requireActiveSession(token);
        if (session.getUser() == null) {
            throw new AuthenticationException("Authenticated user required");
        }
        return session.getUser();
    }

    @Transactional
    public void expireSession(String token) {
        UserSession session = requireActiveSession(token);
        session.setExpiresAt(LocalDateTime.now().minusSeconds(1));
        userSessionRepository.save(session);
    }

    private String generateUniqueToken() {
        String token;
        do {
            token = sessionTokenGenerator.generate();
        } while (userSessionRepository.existsBySessionToken(token));
        return token;
    }
}
