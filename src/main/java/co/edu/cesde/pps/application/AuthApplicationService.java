package co.edu.cesde.pps.application;

import co.edu.cesde.pps.dto.CartDTO;
import co.edu.cesde.pps.dto.UserDTO;
import co.edu.cesde.pps.exception.AuthenticationException;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.security.PasswordHasher;
import co.edu.cesde.pps.service.CartService;
import co.edu.cesde.pps.service.UserService;
import co.edu.cesde.pps.service.UserSessionService;
import co.edu.cesde.pps.web.dto.request.LoginRequest;
import co.edu.cesde.pps.web.dto.request.RegisterRequest;
import co.edu.cesde.pps.web.dto.response.AuthSessionResponse;
import co.edu.cesde.pps.web.dto.response.CartResponse;
import co.edu.cesde.pps.web.dto.response.UserResponse;
import co.edu.cesde.pps.web.mapper.WebResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthApplicationService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserSessionService userSessionService;
    private final CartService cartService;
    private final PasswordHasher passwordHasher;
    private final WebResponseMapper responseMapper;

    public AuthApplicationService(UserService userService,
                                  UserRepository userRepository,
                                  UserSessionService userSessionService,
                                  CartService cartService,
                                  PasswordHasher passwordHasher,
                                  WebResponseMapper responseMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userSessionService = userSessionService;
        this.cartService = cartService;
        this.passwordHasher = passwordHasher;
        this.responseMapper = responseMapper;
    }

    @Transactional
    public AuthSessionResponse createGuestSession() {
        UserSession session = userSessionService.createGuestSession();
        CartDTO cart = cartService.createCartForGuest(session.getSessionId());
        return new AuthSessionResponse(
                session.getSessionToken(),
                null,
                responseMapper.toCartResponse(cart));
    }

    @Transactional
    public AuthSessionResponse register(RegisterRequest request) {
        String passwordHash = passwordHasher.hash(request.password());
        UserDTO userDto = userService.registerUser(
                request.email(),
                passwordHash,
                request.firstName(),
                request.lastName(),
                request.phone());

        User user = userService.findUserEntityOrThrow(userDto.getUserId());
        UserSession session = userSessionService.createAuthenticatedSession(user);
        CartDTO cart = resolveCartAfterAuthentication(user.getUserId(), request.guestCartId());

        return new AuthSessionResponse(
                session.getSessionToken(),
                responseMapper.toUserResponse(userDto),
                responseMapper.toCartResponse(cart));
    }

    @Transactional
    public AuthSessionResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordHasher.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }

        UserSession session = userSessionService.createAuthenticatedSession(user);
        UserDTO userDto = userService.findByEmail(user.getEmail());
        CartDTO cart = resolveCartAfterAuthentication(user.getUserId(), request.guestCartId());

        return new AuthSessionResponse(
                session.getSessionToken(),
                responseMapper.toUserResponse(userDto),
                responseMapper.toCartResponse(cart));
    }

    public UserResponse getCurrentUser(String token) {
        User user = userSessionService.requireAuthenticatedUser(token);
        return responseMapper.toUserResponse(userService.findById(user.getUserId()));
    }

    @Transactional
    public void logout(String token) {
        userSessionService.expireSession(token);
    }

    private CartDTO resolveCartAfterAuthentication(Long userId, Long guestCartId) {
        if (guestCartId != null) {
            return cartService.mergeGuestCartToUserCart(guestCartId, userId);
        }

        CartDTO cart = cartService.findOpenCartByUser(userId);
        return cart != null ? cart : cartService.createCartForUser(userId);
    }
}
