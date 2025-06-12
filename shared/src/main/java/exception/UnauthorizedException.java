package exception;

/**
 *  Indicates an authToken was not found
 */

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
