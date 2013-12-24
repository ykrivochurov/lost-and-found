package ru.eastbanctech.board.core.dao.impl;

import org.springframework.util.CollectionUtils;
import ru.eastbanctech.board.core.dao.MeetingRepositoryCustom;
import ru.eastbanctech.board.core.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 19.05.13
 * Time: 17:30
 */
public class MeetingRepositoryImpl implements MeetingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Meeting> findByCompanyIn(User user, List<Company> companies, Date fromDate,
                                         Date toDate, List<MeetingStatus> statuses) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Meeting> criteriaQuery = criteriaBuilder.createQuery(Meeting.class).distinct(true);
        Root<Meeting> meetingRoot = criteriaQuery.from(Meeting.class);
        Join<Collection<Committee>, Committee> committeeJoin =
                meetingRoot.join("committees", JoinType.LEFT);
        Join<Collection<User>, User> userJoin = committeeJoin.join("users", JoinType.LEFT);

        criteriaQuery = criteriaQuery.select(meetingRoot);
        List<Predicate> where = new ArrayList<>();

        if (user != null) {
            where.add(criteriaBuilder.equal(userJoin.get("id"), user.getId()));
        }

        if (!CollectionUtils.isEmpty(companies)) {
            where.add(criteriaBuilder.in(meetingRoot.get("company")).value(companies));
        }

        if (fromDate != null) {
            where.add(criteriaBuilder.greaterThanOrEqualTo(meetingRoot.<Date>get("dateAndTime"), fromDate));
        }

        if (toDate != null) {
            where.add(criteriaBuilder.lessThan(meetingRoot.<Date>get("dateAndTime"), toDate));
        }

        if (!CollectionUtils.isEmpty(statuses)) {
            where.add(criteriaBuilder.in(meetingRoot.get("status")).value(statuses));
        }

        where.add(criteriaBuilder.notEqual(meetingRoot.<MeetingStatus>get("status"), MeetingStatus.TEMP));

        if (where.isEmpty()) {
            return entityManager.createQuery(criteriaQuery.select(meetingRoot)).getResultList();
        } else {
            Predicate predicate = null;
            for (Predicate predicate1 : where) {
                if (predicate == null) {
                    predicate = criteriaBuilder.and(predicate1);
                } else {
                    predicate = criteriaBuilder.and(predicate, predicate1);
                }
            }

            return entityManager.createQuery(criteriaQuery.select(meetingRoot).
                    where(criteriaBuilder.and(predicate))).getResultList();
        }
    }

}