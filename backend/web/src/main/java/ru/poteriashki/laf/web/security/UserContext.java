package ru.poteriashki.laf.web.security;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import ru.poteriashki.laf.core.model.User;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Krivochurov
 * Date: 17.09.12
 * Time: 23:32
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext implements Serializable {

    private User user;

    private Date checkedAt;

    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checkedAt) {
        this.checkedAt = checkedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
