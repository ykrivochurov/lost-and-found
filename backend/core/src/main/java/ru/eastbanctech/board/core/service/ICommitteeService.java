package ru.eastbanctech.board.core.service;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.CommitteeStatus;

import java.util.List;

/**
 * User: y.krivochurov
 * Date: 04.05.13
 * Time: 16:46
 */
public interface ICommitteeService {

    //    ------------------- Services for secretary -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Committee create(Committee committee) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    void addUser(Long userId, Long committeeId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    void deleteUser(Long userId, Long committeeId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Committee update(Committee committee) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Committee changeStatus(Long committeeId, CommitteeStatus status) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    void delete(Long id) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Committee loadOne(Long id) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    boolean canDelete(Long committeeId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    boolean canArchive(Long committeeId) throws ServiceException;

    //    ------------------- Services for user -------------------

    List<Committee> refreshCommittees(List<Committee> committees) throws ServiceException;

    List<Committee> findByCompany(Long companyId) throws ServiceException;
}
