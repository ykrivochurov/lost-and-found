package ru.eastbanctech.board.web.controllers;

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
import ru.eastbanctech.board.core.dao.CompanyRepository;
import ru.eastbanctech.board.core.model.BaseEntity;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.CompanyStatus;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.web.config.jsonview.ResponseView;

import javax.servlet.http.HttpServletResponse;

/**
 * User: y.krivochurov
 * Date: 05.05.13
 * Time: 17:05
 */
@Controller
@RequestMapping(value = "/api/companies", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ICompanyService companyService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ResponseView(Company.CompanyView.class)
    public Iterable<Company> getCompanies() {

        return companyRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseView(Company.CompanyView.class)
    public Company get(@PathVariable("id") Long companyId) throws ServiceException {

        return companyService.loadOne(companyId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ResponseView(Company.CompanyView.class)
    public Company update(@RequestBody Company company) throws ServiceException {

        return companyService.update(company);
    }

    @RequestMapping(value = "/{id}/user", method = RequestMethod.POST)
    public void addUser(@PathVariable("id") Long id, @RequestParam("userId") Long userId,
                        @RequestParam("position") String position,
                        HttpServletResponse response) throws ServiceException {

        companyService.updateUserWithPosition(id, position, userId);
    }

    @RequestMapping(value = "/{id}/status/{status}", method = RequestMethod.POST)
    public void updateStatus(@PathVariable Long id, @PathVariable CompanyStatus status,
                             HttpServletResponse response) throws ServiceException {

        companyService.changeStatus(id, status);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @ResponseView(Company.CompanyView.class)
    public Company create(@RequestBody Company company) throws ServiceException {

        return companyService.create(company);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteCompany(@PathVariable("id") Long companyId,
                              HttpServletResponse response) throws ServiceException {

        companyService.delete(companyId);
    }

}
