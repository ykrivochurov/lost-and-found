package ru.eastbanctech.board.web.security;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.eastbanctech.board.core.model.User;

/**
 * User: y.krivochurov
 * Date: 26.05.13
 * Time: 17:19
 */
public class SecurityHelper {

    public static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getUser();
        }
        return null;
    }


}
