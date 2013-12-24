package ru.eastbanctech.board.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.BaseEntity;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.web.config.jsonview.ResponseView;
import ru.eastbanctech.board.web.security.SecurityHelper;

/**
 * User: a.zhukov
 * Date: 11.06.13
 * Time: 11:13
 */

@Controller
@RequestMapping("/api/users")
@PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICommitteeService committeeService;

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    @ResponseView(BaseEntity.ListView.class)
    Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    @ResponseBody
    @ResponseView(BaseEntity.DetailView.class)
    public User getUser(@PathVariable("id") Long id) throws ServiceException {
        return userService.loadOne(id);
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    @ResponseView(BaseEntity.ListView.class)
    @ResponseBody
    public User getCurrentUser() throws ServiceException {
        return SecurityHelper.getCurrentUser();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public
    @ResponseBody
    @ResponseView(BaseEntity.DetailView.class)
    User create(@RequestBody User user) throws ServiceException {
        return userService.create(user);
    }

    @RequestMapping(value = "/company/{companyId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    @ResponseView(User.UserView.class)
    public
    @ResponseBody
    Iterable<User> getToAddToCompany(@PathVariable("companyId") Long companyId) throws ServiceException {
        return userService.getByCompanyNotIn(companyId);
    }

    @RequestMapping(value = "/committee/{committeeId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    @ResponseView(User.UserView.class)
    public
    @ResponseBody
    Iterable<User> getToAddToCommittee(@PathVariable("committeeId") Long committeeId) throws ServiceException {
        Committee committee = committeeService.loadOne(committeeId);
        return userService.getByCompanyInAndAllCompanyCommitteesNotIn(committee.getCompany().getId());
    }
}
