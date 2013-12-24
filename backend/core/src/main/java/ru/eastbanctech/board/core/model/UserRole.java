package ru.eastbanctech.board.core.model;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 11:35
 */
public enum UserRole {

    ROLE_USER,
    ROLE_SECRETARY;

    public static boolean contains(String test) {

        for (UserRole c : UserRole.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }

}
