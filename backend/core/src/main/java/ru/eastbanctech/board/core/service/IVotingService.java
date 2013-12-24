package ru.eastbanctech.board.core.service;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.Vote;

/**
 * User: a.zhukov
 * Date: 14.06.13
 * Time: 17:40
 */
public interface IVotingService {

    //    ------------------- Services for secretary -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    void delete(Long id) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Vote update(Vote vote) throws ServiceException;

    //    ------------------- Services for users -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Vote create(Vote vote) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Vote findById(Long id, User user) throws ServiceException;
}
