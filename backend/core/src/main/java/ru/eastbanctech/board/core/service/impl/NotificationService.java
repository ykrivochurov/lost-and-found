package ru.eastbanctech.board.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.eastbanctech.board.core.dao.NotificationRepository;
import ru.eastbanctech.board.core.model.Notification;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.INotificationService;

import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 14:56
 */
@Service
@Transactional
public class NotificationService implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Notification create(Notification notification, User user) {
        Assert.notNull(notification);
        Assert.notNull(user);

        notification.setUser(user);
        notification.setCreationDate(new Date());
        notification.setVisited(false);

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> lastUnreadNotifications(User user, Integer pageNumber, Integer count) {
        Assert.notNull(count);
        Assert.notNull(user);

        return notificationRepository.findByUserAndVisitedOrderByCreationDateDesc(user, Boolean.FALSE,
                new PageRequest(pageNumber, count)).getContent();
    }

    @Override
    public Long countUnreadNotifications(User user) {
        Assert.notNull(user);

        return notificationRepository.countByUserAndVisited(user, Boolean.FALSE);
    }

}