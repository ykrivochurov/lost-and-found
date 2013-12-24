package ru.eastbanctech.board.core.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.eastbanctech.board.core.model.Notification;
import ru.eastbanctech.board.core.model.User;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 14:54
 */
public interface NotificationRepository extends PagingAndSortingRepository<Notification, Long> {

    Page<Notification> findByUserAndVisitedOrderByCreationDateDesc(User user, Boolean read, Pageable pageable);

    @Query("select count(notification) from Notification notification " +
            "where notification.user = ?1 and notification.visited = ?2")
    Long countByUserAndVisited(User user, Boolean read);

}