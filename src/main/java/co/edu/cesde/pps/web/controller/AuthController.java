package co.edu.cesde.pps.web.controller;

import co.edu.cesde.pps.application.AuthApplicationService;
import co.edu.cesde.pps.web.dto.request.LoginRequest;
import co.edu.cesde.pps.web.dto.request.RegisterRequest;
import co.edu.cesde.pps.web.dto.response.AuthSessionResponse;
import co.edu.cesde.pps.web.dto.response.UserResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutes.AUTH)
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public AuthController(AuthApplicationService authApplicationService,
                          CurrentSessionResolver currentSessionResolver) {
        this.authApplicationService = authApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @PostMapping("/guest-session")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthSessionResponse createGuestSession() {
        return authApplicationService.createGuestSession();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthSessionResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authApplicationService.register(request));
    }

    @PostMapping("/login")
    public AuthSessionResponse login(@Valid @RequestBody LoginRequest request) {
        return authApplicationService.login(request);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        return authApplicationService.getCurrentUser(currentSessionResolver.resolveCurrentToken(auth));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        authApplicationService.logout(currentSessionResolver.resolveCurrentToken(auth));
        return ResponseEntity.noContent().build();
    }
}
