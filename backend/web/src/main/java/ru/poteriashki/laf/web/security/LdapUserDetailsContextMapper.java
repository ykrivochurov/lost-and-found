package ru.poteriashki.laf.web.security;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;
import ru.poteriashki.laf.core.model.UserRole;
import ru.poteriashki.laf.core.service.IUserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
@Component
public class LdapUserDetailsContextMapper implements UserDetailsContextMapper {

    @Autowired
    private IUserService userService;

    @Override
    public CustomUserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {

        Collection<GrantedAuthority> new_authorities = new ArrayList<>();
        new_authorities.addAll(authorities);
        new_authorities.add(new SimpleGrantedAuthority("ROLE_USER"));


        User user = new User(username, "", true, true, true, true, new_authorities);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Set<UserRole> userRoles = Sets.newHashSet();
        for (GrantedAuthority authority: new_authorities) {
            if (UserRole.contains(authority.getAuthority())) {
                userRoles.add(UserRole.valueOf(authority.getAuthority()));
            }
        }
        userDetails.getUser().setRoles(userRoles);
        userService.createOrUpdate(userDetails.getUser());
        return userDetails;
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {

    }
}
