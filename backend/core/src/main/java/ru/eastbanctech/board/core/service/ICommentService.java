package ru.eastbanctech.board.core.service;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.eastbanctech.board.core.model.Comment;
import ru.eastbanctech.board.core.model.User;

import java.util.List;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 16:05
 */
public interface ICommentService {

    //    ------------------- Services for secretary -------------------
/*  todo: useless??
    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    List<Comment> lastUnreadCommentsAllMeetings(User user, int count);*/

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    List<Object[]> unreadCommentsCountGroupedByQuestionAllMeetings(User user, int count);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    Long countOfUnreadAllMeetings(User user);

    //    ------------------- Services for user -------------------
/*  todo: useless??
    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    List<Comment> lastUnreadComments(User user, int count);*/

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    List<Object[]> unreadCommentsCountGroupedByQuestion(User user, int count);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Long countOfUnread(User user);

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    Comment create(Comment comment, User user) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    List<Comment> loadByQuestion(Long questionId, Pageable pageable, User user) throws ServiceException;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    void delete(Long commentId, User user) throws ServiceException;
}
