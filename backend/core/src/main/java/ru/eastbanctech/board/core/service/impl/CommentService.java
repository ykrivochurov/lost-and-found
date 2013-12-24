package ru.eastbanctech.board.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.eastbanctech.board.core.dao.CommentRepository;
import ru.eastbanctech.board.core.dao.UserVisitQuestionRepository;
import ru.eastbanctech.board.core.model.Comment;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserRole;
import ru.eastbanctech.board.core.model.UserVisitQuestion;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.ICommentService;
import ru.eastbanctech.board.core.service.IQuestionService;
import ru.eastbanctech.board.core.service.ServiceException;

import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 16:05
 */
@Service
@Transactional
public class CommentService implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private UserVisitQuestionRepository userVisitQuestionRepository;

    private void checkAccessToComment(User user, Question question) throws ServiceException {
        if (user.getRoles().contains(UserRole.ROLE_SECRETARY)) {
            return;
        }
        boolean allowAccess = false;
        for (Committee committee : question.getMeeting().getCommittees()) {
            if (committee.getUsers().contains(user)) {
                allowAccess = true;
            }
        }

        if (!allowAccess) {
            throw new ServiceException(ErrorType.ACCESS_DENIED,
                    "Only secretary or user from meeting have access to comments");
        }
    }

    @Override
    public Comment create(Comment comment, User user) throws ServiceException {
        Assert.notNull(comment, "'comment' parameter shouldn't be empty");
        Assert.notNull(user, "'user' parameter shouldn't be empty");
        Assert.notNull(comment.getQuestion(), "'question' parameter shouldn't be empty");
        Assert.notNull(comment.getText(), "'text' parameter shouldn't be empty");
        Assert.notNull(comment.getQuestion().getId(), "'questionId' parameter shouldn't be empty");

        Question question = questionService.loadOne(comment.getQuestion().getId());

        checkAccessToComment(user, question);

        comment.setCreationDate(new Date());
        comment.setAuthor(user);
        comment.setQuestion(question);

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> loadByQuestion(Long questionId, Pageable pageable, User user) throws ServiceException {
        Assert.notNull(user, "'user' parameter shouldn't be empty");
        Assert.notNull(questionId, "'questionId' parameter shouldn't be empty");

        Question question = questionService.loadOne(questionId);

        checkAccessToComment(user, question);

        List<Comment> comments =
                commentRepository.findByQuestionIdOrderByCreationDateDesc(questionId, pageable).getContent();

        UserVisitQuestion visitQuestion = userVisitQuestionRepository.
                findByUserQuestionUserIdAndUserQuestionQuestionId(user.getId(), questionId);

        for (Comment comment : comments) {
            comment.setNotVisited(visitQuestion == null ||
                (comment.getCreationDate().after(visitQuestion.getLastVisit()) && !comment.getAuthor().equals(user)));
        }

        return comments;
    }

    @Override
    public void delete(Long commentId, User user) throws ServiceException {
        Assert.notNull(commentId, "'commentId' parameter shouldn't be empty");
        Assert.notNull(user, "'user' parameter shouldn't be empty");

        Comment comment = commentRepository.findOne(commentId);
        if (!user.getRoles().contains(UserRole.ROLE_SECRETARY) ||
                !user.equals(comment.getAuthor())) {
            throw new ServiceException(ErrorType.ACCESS_DENIED,
                    "Only secretary or comment author can delete comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    public List<Object[]> unreadCommentsCountGroupedByQuestion(User user, int count) {
        Assert.notNull(user);

        return commentRepository.countsOfUnread(user,
                new PageRequest(0, count)).getContent();
    }

    @Override
    public List<Object[]> unreadCommentsCountGroupedByQuestionAllMeetings(User user, int count) {
        Assert.notNull(user);
        return commentRepository.countsOfUnreadInAllMeetings(user,
                new PageRequest(0, count)).getContent();
    }

    @Override
    public Long countOfUnread(User user) {
        Assert.notNull(user);

        return commentRepository.countOfUnread(user);
    }

    @Override
    public Long countOfUnreadAllMeetings(User user) {
        Assert.notNull(user);
        return commentRepository.countOfUnreadInAllMeetings(user);
    }

/*  todo: useless??
    @Override
    public List<Comment> lastUnreadComments(User user, int count) {
        Assert.notNull(user);
        return commentRepository.lastUnread(user,
                new PageRequest(0, count, new Sort(Sort.Direction.DESC, Comment.DATE_FIELD))).getContent();
    }

    @Override
    public List<Comment> lastUnreadCommentsAllMeetings(User user, int count) {
        Assert.notNull(user);

        return commentRepository.lastUnreadInAllMeetings(user,
                new PageRequest(0, count, new Sort(Sort.Direction.DESC, Comment.DATE_FIELD))).getContent();
    }*/
}