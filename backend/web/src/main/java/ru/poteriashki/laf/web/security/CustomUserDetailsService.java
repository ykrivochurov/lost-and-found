package ru.poteriashki.laf.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.service.IUserService;

/**
 * User: y.krivochurov
 * Date: 26.05.13
 * Time: 13:48
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userService.loadByName(userName);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", userName));
        }
        return new CustomUserDetails(user);
    }
}
