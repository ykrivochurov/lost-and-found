package ru.eastbanctech.board.core.service;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.eastbanctech.board.core.model.UserPositionInCompany;

import java.util.List;

/**
 * User: a.zhukov
 * Date: 11.06.13
 * Time: 15:22
 */
public interface IUserPositionInCompanyService {

    //    ------------------- Services for secretary -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    UserPositionInCompany create(UserPositionInCompany position) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    UserPositionInCompany update(UserPositionInCompany position) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    void delete(Long companyId, Long userId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Page<UserPositionInCompany> findByCommitteeId(Long committeeId, Integer pageNumber, Integer pageCount)
            throws ServiceException;

    //    ------------------- Services for users -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Page<UserPositionInCompany> findByCompanyId(Long companyId, Integer pageNumber, Integer pageCount)
            throws ServiceException;
}
