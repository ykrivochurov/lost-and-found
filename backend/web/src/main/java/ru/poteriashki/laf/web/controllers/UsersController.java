package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.service.IUserService;
import ru.poteriashki.laf.web.security.UserContext;

@Controller
@RequestMapping(value = "/api/users")
public class UsersController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserContext userContext;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public User update(@RequestBody User user) {
        User currentUser = userContext.getUser();
        currentUser.setName(user.getName());
        currentUser.setPhone(user.getPhone());
        currentUser.setEmail(user.getEmail());
        return userService.updateUser(currentUser);
    }

}