package ru.eastbanctech.board.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.IUserService;

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
