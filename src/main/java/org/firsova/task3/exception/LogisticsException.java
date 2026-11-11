package org.firsova.task3.exception;

public class LogisticsException extends Exception {
    public LogisticsException() {
        super();
    }

    public LogisticsException(String message) {
        super(message);
    }

    public LogisticsException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogisticsException(Throwable cause) {
        super(cause);
    }
}
