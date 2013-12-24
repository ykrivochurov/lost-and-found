package ru.eastbanctech.board.core.service;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.eastbanctech.board.core.model.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
public interface IUserService {

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    User create(User user) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    List<User> getByCompanyNotIn(Long companyId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    List<User> getByCompanyInAndAllCompanyCommitteesNotIn(Long companyId) throws ServiceException;

    User createOrUpdate(User user);

    User loadByName(String userName);

    User loadOne(Long id) throws ServiceException;
}
