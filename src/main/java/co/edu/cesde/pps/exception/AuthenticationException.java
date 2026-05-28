package co.edu.cesde.pps.exception;

/**
 * Excepcion para fallos de autenticacion o sesion.
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message) {
        super(message);
    }
}
