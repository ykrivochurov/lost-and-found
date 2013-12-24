package ru.eastbanctech.board.core.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;

import java.util.List;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 13:21
 */
public interface CommitteeRepository extends CrudRepository<Committee, Long> {

    List<Committee> findByCompanyId(Long companyId);

    @Query("select committee from Committee committee " +
            "where committee in ?1")
    List<Committee> refreshCommittees(List<Committee> committees);

}
