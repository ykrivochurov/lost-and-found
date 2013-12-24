package ru.eastbanctech.board.core.service;

public class ServiceException extends Exception {

    private ErrorType type;

    public ServiceException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }

    public ServiceException(ErrorType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public ServiceException(ErrorType type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }

    public String getTypeString() {
        return type.toString().toLowerCase();
    }

}
