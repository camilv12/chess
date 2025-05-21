package service;

/**
 *  Indicates a requested value is already found
 */

public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException(String message) {
        super(message);
    }
}
