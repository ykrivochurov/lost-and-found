package ru.eastbanctech.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.NotificationRepository;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.Notification;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.INotificationService;
import ru.eastbanctech.board.util.TestHelper;

import java.util.List;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 15:00
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class, TestPersistenceJPAConfig.class})
public class NotificationServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private INotificationService notificationService;

    @Test
    public void testCreate() throws Exception {
        User user = TestHelper.createUser();
        userRepository.save(user);

        Notification notification = new Notification();
        notification.setText("Test notification");
        notification = notificationService.create(notification, user);
        Assert.assertNotNull(notification.getId());
    }

    @Test
    public void testLoadLast() throws Exception {
        User user = TestHelper.createUser();
        user = userRepository.save(user);

        for (int i = 0; i < 20; i++) {
            Notification notification = new Notification();
            notification.setText("Test notification");
            notification = notificationService.create(notification, user);
            Assert.assertNotNull(notification.getId());
        }


        Page<Notification> page = notificationRepository.findAll(new PageRequest(0, 10));
        Assert.assertEquals(page.getContent().size(), 10);

        List<Notification> notifications = notificationService.lastUnreadNotifications(user, 0, 10);
        Assert.assertEquals(notifications.size(), 10);
    }

}