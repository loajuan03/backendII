package co.edu.cesde.pps.exception;

/**
 * Excepcion lanzada cuando un usuario autenticado no tiene permisos suficientes.
 */
public class AuthorizationException extends BusinessException {

    public AuthorizationException(String message) {
        super(message);
    }
}
