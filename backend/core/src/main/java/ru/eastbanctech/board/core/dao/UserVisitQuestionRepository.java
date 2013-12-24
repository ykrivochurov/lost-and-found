package ru.eastbanctech.board.core.dao;

import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.UserVisitQuestion;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:43
 */
public interface UserVisitQuestionRepository extends CrudRepository<UserVisitQuestion, Long> {

    UserVisitQuestion findByUserQuestionUserIdAndUserQuestionQuestionId(Long userId, Long questionId);
}