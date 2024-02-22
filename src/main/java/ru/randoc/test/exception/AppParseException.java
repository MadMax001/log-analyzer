package ru.randoc.test.exception;

public class AppParseException extends Exception {
    public AppParseException(Throwable cause) {
        super(cause);
    }

    public AppParseException(String message) {
        super(message);
    }
}
