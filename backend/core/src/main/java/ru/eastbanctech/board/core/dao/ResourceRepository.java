package ru.eastbanctech.board.core.dao;

import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Resource;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:42
 */
public interface ResourceRepository extends CrudRepository<Resource, Long> {
}
