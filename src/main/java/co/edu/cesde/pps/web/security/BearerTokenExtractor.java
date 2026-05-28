package co.edu.cesde.pps.web.security;

import co.edu.cesde.pps.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Extrae el sessionToken del header Authorization: Bearer <token>.
 */
@Component
public class BearerTokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    public String extractRequiredToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new AuthenticationException("Empty Bearer token");
        }

        return token;
    }

    public String extractRequiredToken(HttpServletRequest request) {
        return extractRequiredToken(request.getHeader(HttpHeaders.AUTHORIZATION));
    }
}
