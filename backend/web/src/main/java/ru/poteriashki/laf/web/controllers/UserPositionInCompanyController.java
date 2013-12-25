package ru.poteriashki.laf.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.UserPositionInCompany;
import ru.poteriashki.laf.core.service.IUserPositionInCompanyService;
import ru.poteriashki.laf.core.service.ServiceException;
import ru.poteriashki.laf.web.config.jsonview.ResponseView;

import javax.servlet.http.HttpServletResponse;

/**
 * User: a.zhukov
 * Date: 11.06.13
 * Time: 15:24
 */

@Controller
@RequestMapping("/api/positions")
@PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
public class UserPositionInCompanyController {

    @Autowired
    private IUserPositionInCompanyService userPositionInCompanyService;

    @RequestMapping(value = "/company/{companyId}", method = RequestMethod.GET)
    public
    @ResponseBody
    @ResponseView(UserPositionInCompany.PositionView.class)
    Page<UserPositionInCompany> getPositions(@PathVariable("companyId") Long companyId,
                        @RequestParam("pageNumber") Integer pageNumber, @RequestParam("pageCount") Integer pageCount)
                        throws JsonProcessingException, ServiceException {
        return userPositionInCompanyService.findByCompanyId(companyId, pageNumber, pageCount);
    }

    @RequestMapping(value = "/committee/{committeeId}", method = RequestMethod.GET)
    public
    @ResponseBody
    @ResponseView(UserPositionInCompany.PositionView.class)
    Page<UserPositionInCompany> getPositionsByCommittee(
                        @PathVariable("committeeId") Long committeeId, @RequestParam("pageNumber") Integer pageNumber,
                        @RequestParam("pageCount") Integer pageCount) throws JsonProcessingException, ServiceException {
        return userPositionInCompanyService.findByCommitteeId(committeeId, pageNumber, pageCount);
    }

    @RequestMapping(value = "company/{companyId}/user/{userId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("companyId") Long companyId, @PathVariable("userId") Long userId,
                       HttpServletResponse response) throws ServiceException {
        userPositionInCompanyService.delete(companyId, userId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public
    @ResponseBody
    @ResponseView(UserPositionInCompany.PositionView.class)
    UserPositionInCompany create(@RequestBody UserPositionInCompany position)
                        throws JsonProcessingException, ServiceException {
        return userPositionInCompanyService.create(position);
    }

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    @ResponseView(UserPositionInCompany.PositionView.class)
    UserPositionInCompany update(@RequestBody UserPositionInCompany position)
                        throws JsonProcessingException, ServiceException {
        return userPositionInCompanyService.update(position);
    }

}
