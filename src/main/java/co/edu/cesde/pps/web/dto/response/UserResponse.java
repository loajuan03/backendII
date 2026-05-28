package co.edu.cesde.pps.web.dto.response;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String roleName,
        String status
) {
}
