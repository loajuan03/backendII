package co.edu.cesde.pps.web.security;

import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.service.UserSessionService;
import org.springframework.stereotype.Component;

/**
 * Resuelve la sesion y el usuario autenticado desde el header Authorization.
 */
@Component
public class CurrentSessionResolver {

    private final BearerTokenExtractor bearerTokenExtractor;
    private final UserSessionService userSessionService;

    public CurrentSessionResolver(BearerTokenExtractor extractor,
                                  UserSessionService sessionService) {
        this.bearerTokenExtractor = extractor;
        this.userSessionService = sessionService;
    }

    public String resolveCurrentToken(String authorizationHeader) {
        return bearerTokenExtractor.extractRequiredToken(authorizationHeader);
    }

    public UserSession resolveCurrentSession(String authorizationHeader) {
        return userSessionService.requireActiveSession(resolveCurrentToken(authorizationHeader));
    }

    public User resolveAuthenticatedUser(String authorizationHeader) {
        return userSessionService.requireAuthenticatedUser(resolveCurrentToken(authorizationHeader));
    }
}
