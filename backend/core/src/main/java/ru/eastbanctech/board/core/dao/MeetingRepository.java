package ru.eastbanctech.board.core.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.User;

import java.util.Date;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:04
 */
public interface MeetingRepository extends CrudRepository<Meeting, Long>, MeetingRepositoryCustom {

    @Query("select meeting from Meeting meeting " +
            "join meeting.committees committee " +
            "join committee.users user where user = ?1 and meeting.status <> 'TEMP'")
    Page<Meeting> findByUser(User user, Pageable pageable);

    @Query("select meeting from Meeting meeting " +
            "where meeting.dateAndTime >= ?1 and meeting.status <> 'TEMP'")
    Page<Meeting> findNearest(Date date, Pageable pageable);

    @Query("select meeting from Meeting meeting " +
            "join meeting.committees committee " +
            "join committee.users user " +
            "where user = ?1 and meeting.dateAndTime >= ?2  and meeting.status <> 'TEMP' group by meeting.id")
    Page<Meeting> findNearestByUser(User user, Date date, Pageable pageable);

    @Query("select meeting from Meeting meeting join meeting.committees committee " +
            "where committee = ?1")
    Page<Meeting> findByCommittee(Committee committee, Pageable pageable);

    Page<Meeting> findByCompany(Company company, Pageable pageable);

    @Query("select meeting from Meeting meeting " +
            "where meeting.dateAndTime >= ?2 and meeting.company = ?1")
    Page<Meeting> findNearestByCompany(Company company, Date date, Pageable pageable);

    @Query("select meeting from Meeting meeting join meeting.committees committee " +
            "where meeting.dateAndTime >= ?2 and committee = ?1")
    Page<Meeting> findNearestByCommittee(Committee committee, Date date, Pageable pageable);

}
