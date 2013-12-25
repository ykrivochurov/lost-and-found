package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.Committee;
import ru.poteriashki.laf.core.model.CommitteeStatus;
import ru.poteriashki.laf.core.service.ICommitteeService;
import ru.poteriashki.laf.core.service.ServiceException;
import ru.poteriashki.laf.web.config.jsonview.ResponseView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * User: Krivochurov
 * Date: 04.06.13
 * Time: 23:52
 */
@Controller
@RequestMapping(value = "/api/committees", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommitteeController {

    @Autowired
    private ICommitteeService committeeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseView(Committee.CommitteeView.class)
    public Committee get(@PathVariable("id") Long committeeId) throws ServiceException {
        return committeeService.loadOne(committeeId);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ResponseView(Committee.CommitteeView.class)
    public List<Committee> getByCompany(@RequestParam("companyId") Long companyId) throws ServiceException {
        return committeeService.findByCompany(companyId);
    }

    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @ResponseView(Committee.CommitteeView.class)
    public Committee create(@RequestBody Committee committee) throws ServiceException {
        return committeeService.create(committee);
    }

    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) throws ServiceException {
        committeeService.delete(id);
    }

    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ResponseView(Committee.CommitteeView.class)
    public Committee update(@RequestBody Committee committee) throws ServiceException {
        return committeeService.update(committee);
    }

    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(value = "/{id}/addUser", method = RequestMethod.POST)
    @ResponseBody
    @ResponseView(Committee.CommitteeView.class)
    public void addUser(@PathVariable("id") Long id, @RequestParam("userId") Long userId,
                        HttpServletResponse response) throws ServiceException {
        committeeService.addUser(userId, id);
    }

    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(value = "/{id}/deleteUser", method = RequestMethod.POST)
    @ResponseBody
    @ResponseView(Committee.CommitteeView.class)
    public void deleteUser(@PathVariable("id") Long id, @RequestParam("userId") Long userId,
                           HttpServletResponse response) throws ServiceException {
        committeeService.deleteUser(userId, id);
    }

    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(value = "/{id}/status/{status}", method = RequestMethod.POST)
    public void changeStatus(@PathVariable Long id, @PathVariable CommitteeStatus status,
                             HttpServletResponse response) throws ServiceException {

        committeeService.changeStatus(id, status);
    }
}
