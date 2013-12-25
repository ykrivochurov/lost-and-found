package ru.poteriashki.laf.web.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.web.controllers.dtos.LoginStatus;


/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
@Controller
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccessController {

    @RequestMapping(method = RequestMethod.GET, value = "/login-status")
    @ResponseBody
    public LoginStatus getStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return new LoginStatus("success", true, authentication.getName());
        } else {
            return new LoginStatus("not_authenticated", false, null);
        }
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(method = RequestMethod.GET, value = "/check-auth")
    @ResponseBody
    public LoginStatus checkAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new LoginStatus("success", true, authentication.getName());
    }

    @RequestMapping("/login-process")
    @ResponseBody
    public LoginStatus login() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return new LoginStatus("success", true, authentication.getName());
        } else {
            return new LoginStatus("bad_credentials", false, null);
        }
    }

    @RequestMapping("/logout-process")
    @ResponseBody
    public LoginStatus logout() {
        return new LoginStatus("not_authenticated", false, null);
    }
}
