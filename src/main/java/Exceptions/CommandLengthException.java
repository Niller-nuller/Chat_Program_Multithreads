package Exceptions;

public class CommandLengthException extends RuntimeException {
    public CommandLengthException(String message) {
        super(message);
    }
}
