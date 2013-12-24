package ru.eastbanctech.board.core.service;

import ru.eastbanctech.board.core.model.Notification;
import ru.eastbanctech.board.core.model.User;

import java.util.List;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 14:55
 */
public interface INotificationService {

    List<Notification> lastUnreadNotifications(User user, Integer pageNumber, Integer count);

    Notification create(Notification notification, User user);

    Long countUnreadNotifications(User user);
}