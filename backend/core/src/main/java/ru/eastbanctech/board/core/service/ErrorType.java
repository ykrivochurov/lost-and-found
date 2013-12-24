package ru.eastbanctech.board.core.service;

public enum ErrorType {
    OBJECT_NOT_FOUND,
    DATABASE_ERROR,
    ACCESS_DENIED,
    CONFLICT,
    UNKNOWN,
    DESYNC,
    MESSAGING_ERROR,
    INVALID_INPUT_DATA,
    WEB_SOCKET,
    LDAP
}
