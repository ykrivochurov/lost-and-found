package ru.eastbanctech.board.core.dao;

import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Vote;
import ru.eastbanctech.board.core.model.Vote;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:44
 */
public interface VoteRepository extends CrudRepository<Vote, Long> {
}
