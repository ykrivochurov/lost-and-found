package ru.eastbanctech.board.web.controllers.dtos;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
public class LoginStatus {

    private final boolean loggedIn;

    private final String username;

    private final String status;

    public LoginStatus(String status, boolean loggedIn, String username) {

        this.status = status;
        this.loggedIn = loggedIn;
        this.username = username;

    }

    public boolean isLoggedIn() {

        return loggedIn;
    }

    public String getUsername() {

        return username;
    }

    public String getStatus() {

        return status;
    }
}
