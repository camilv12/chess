package service;

/**
 *  Indicates an error with processing the request
 */

public class BadRequestException extends Exception{
    public BadRequestException(String message) { super(message); }
    public BadRequestException(String message, Throwable ex) { super(message, ex); }
}
