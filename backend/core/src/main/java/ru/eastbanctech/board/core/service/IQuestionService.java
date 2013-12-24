package ru.eastbanctech.board.core.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;

import javax.servlet.http.HttpServletResponse;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 16:21
 */
public interface IQuestionService {

    //    ------------------- Services for secretary -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    void delete(Long id);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Question create(Question question, User user) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    void deleteDocument(Long id) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Question getByMeetingIdAndNumber(Long meetingId, Integer questNum);

    //    ------------------- Services for user -------------------

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Question getByIdAndUser(Long questionId, User user) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    void userVisitQuestion(User user, Question question);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Question update(Question question, User user) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    void uploadDocument(User user, MultipartFile document, Long questionId) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    void loadDocument(User user, Long id, HttpServletResponse response) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    void loadZippedDocuments(User user, Long questionId, HttpServletResponse response) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Question loadOne(Long questionId) throws ServiceException;
}