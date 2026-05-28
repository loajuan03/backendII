package co.edu.cesde.pps.web.dto.response;

public record AuthSessionResponse(String sessionToken, UserResponse user, CartResponse cart) {
}
