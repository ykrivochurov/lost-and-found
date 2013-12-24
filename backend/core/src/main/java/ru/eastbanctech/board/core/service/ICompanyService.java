package ru.eastbanctech.board.core.service;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.CompanyStatus;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;

/**
 * User: y.krivochurov
 * Date: 04.05.13
 * Time: 15:43
 */
public interface ICompanyService {

    //    ------------------- Services for secretary -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    UserPositionInCompany updateUserWithPosition(Long companyId, String position, Long userId);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Company create(Company company) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Company update(Company company) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Company changeStatus(Long companyId, CompanyStatus status) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    void delete(Long companyId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    boolean canDelete(Long companyId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    boolean canArchive(Long companyId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Iterable<Company> loadActiveCompanies();

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Iterable<Company> loadAll();

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Company loadOne(Long id) throws ServiceException;

    Iterable<Company> loadActiveCompaniesByUser(User user);


}
