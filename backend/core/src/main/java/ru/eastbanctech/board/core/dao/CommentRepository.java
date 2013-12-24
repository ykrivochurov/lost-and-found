package ru.eastbanctech.board.core.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Comment;
import ru.eastbanctech.board.core.model.User;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:41
 */
public interface CommentRepository extends CrudRepository<Comment, Long> {

/* todo: useless??
    @Query("select comment from Comment comment join comment.question question " +
            "join question.meeting meeting " +
            "join meeting.committees committee " +
            "join committee.users user " +
            "left outer join user.userVisitQuestions userVisit " +
            "where user = ?1 and userVisit.userQuestion.user = ?1 " +
            "and (comment.creationDate > userVisit.lastVisit)")
    Page<Comment> lastUnread(User user, Pageable pageable);

    @Query("select comment from Comment comment join comment.question question " +
            "left outer join question.userVisitQuestions userVisit " +
            "where userVisit = null or (userVisit.userQuestion.user = ?1 " +
            "and (comment.creationDate > userVisit.lastVisit))")
    Page<Comment> lastUnreadInAllMeetings(User user, Pageable pageable);*/

    @Query("select question, count(comment) " +
            "from Comment comment join comment.question question " +
            "left outer join question.userVisitQuestions userVisit " +
            "where userVisit is null " +
            "or ?1 not in (select user from question.userVisitQuestions uv join uv.userQuestion.user user) " +
            "or ((userVisit.userQuestion.user = ?1) " +
            "and (comment.creationDate > userVisit.lastVisit) " +
            "and (comment.author <> ?1)) " +
            "group by question")
    Page<Object[]> countsOfUnreadInAllMeetings(User user, Pageable pageable);

    @Query("select count(comment) " +
            "from Comment comment join comment.question question " +
            "left outer join question.userVisitQuestions userVisit " +
            "where userVisit is null " +
            "or ?1 not in (select user from question.userVisitQuestions uv join uv.userQuestion.user user) " +
            "or ((userVisit.userQuestion.user = ?1) " +
            "and (comment.creationDate > userVisit.lastVisit) " +
            "and (comment.author <> ?1))")
    Long countOfUnreadInAllMeetings(User user);

    @Query("select question, count(comment) " +
            "from Comment comment join comment.question question " +
            "join question.committee committee " +
            "left outer join question.userVisitQuestions userVisit " +
            "where ?1 member of committee.users " +
            "and (userVisit is null " +
            "or ?1 not in (select user from question.userVisitQuestions uv join uv.userQuestion.user user) " +
            "or ((userVisit.userQuestion.user = ?1) " +
            "and (comment.creationDate > userVisit.lastVisit) " +
            "and (comment.author <> ?1)))" +
            "group by question")
    Page<Object[]> countsOfUnread(User user, Pageable pageable);

    @Query("select count(comment) " +
            "from Comment comment join comment.question question " +
            "join question.committee committee " +
            "left outer join question.userVisitQuestions userVisit " +
            "where ?1 member of committee.users " +
            "and (userVisit is null " +
            "or ?1 not in (select user from question.userVisitQuestions uv join uv.userQuestion.user user) " +
            "or ((userVisit.userQuestion.user = ?1) " +
            "and (comment.creationDate > userVisit.lastVisit) " +
            "and (comment.author <> ?1)))")
    Long countOfUnread(User user);

    Page<Comment> findByQuestionIdOrderByCreationDateDesc(Long questionId, Pageable pageable);
}
