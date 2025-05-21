package service;

/**
 *  Indicates an error with processing the request
 */

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) { super(message); }
}
