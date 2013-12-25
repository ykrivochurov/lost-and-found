package ru.poteriashki.laf.web.security;

import org.springframework.security.core.userdetails.User;

/**
 * User: y.krivochurov
 * Date: 26.05.13
 * Time: 13:50
 */
public class CustomUserDetails extends User {

    private ru.poteriashki.laf.core.model.User user;

    public CustomUserDetails(ru.poteriashki.laf.core.model.User user) {
        super(user.getLogin(), user.getPassword(), user.getAuthorities());
        this.user = user;
    }

    public CustomUserDetails(User user) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
        this.user = new ru.poteriashki.laf.core.model.User();
        this.user.setLogin(user.getUsername());

    }

    public ru.poteriashki.laf.core.model.User getUser() {
        return user;
    }
}
