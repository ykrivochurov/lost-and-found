package ru.eastbanctech.board.core.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;

import java.util.List;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:05
 */
public interface QuestionRepository extends CrudRepository<Question, Long> {

    @Query("select question from Question question join question.meeting meeting join meeting.committees committee " +
            "join committee.users user where user = ?1")
    Page<Question> findQuestionsByUser(User user, Pageable pageable);

    List<Question> findByMeetingIdOrderByNumberAsc(Long meetingId);

    @Modifying
    @Query("update Question question set question.number = ?1 where question.id = ?2")
    void updateNumber(Integer number, Long id);

    Question findByMeetingIdAndNumber(Long meetingId, Integer questNum);
}
