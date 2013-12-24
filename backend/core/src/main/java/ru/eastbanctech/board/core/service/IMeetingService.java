package ru.eastbanctech.board.core.service;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.MeetingStatus;
import ru.eastbanctech.board.core.model.User;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 18:11
 */
public interface IMeetingService {

    //    ------------------- Services for secretary -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Meeting createTemp(User user, Long companyId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Meeting update(Meeting meeting) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    List<Meeting> findNearest(int count);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    List<Meeting> findByFilter(List<MeetingStatus> statuses, List<Company> companies,
                               Date fromDate, Date toDate);

    //    ------------------- Services for users -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Meeting loadOne(Long id) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    List<Meeting> findNearestByUser(User user, int count);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    List<Meeting> findByFilterAndUser(User user, List<MeetingStatus> statuses, List<Company> companies,
                                      Date fromDate, Date toDate);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    void loadAgenda(User user, Long id, HttpServletResponse response) throws ServiceException;
}