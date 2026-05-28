package co.edu.cesde.pps.security;

/**
 * Abstraccion para hashing y verificacion de contrasenas.
 */
public interface PasswordHasher {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
