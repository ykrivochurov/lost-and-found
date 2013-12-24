package ru.eastbanctech.board.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.User;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 11:49
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class, TestPersistenceJPAConfig.class})
public class UserRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreate() throws Exception {

        User user = new User();
        user.setFirstName("John");
        user.setMiddleName("A");
        user.setLastName("Doe");
        user.setLogin("jad");
        user.setPassword("test");
        user.setPhone("+7 (777) 11-22-3333");
        user.setEmail("test@email.com");

        user = userRepository.save(user);

        Assert.assertNotNull(user.getId());
    }


    @Test
    public void findByUserToken_shouldFindTheUser() throws Exception {

        User user = new User();
        user.setFirstName("John");
        user.setMiddleName("A");
        user.setLastName("Doe");
        user.setLogin("jad");
        user.setPassword("test");
        user.setPhone("+7 (777) 11-22-3333");
        user.setEmail("test@email.com");

        userRepository.save(user);

        User dbUser = userRepository.findByLogin("jad");

        Assert.assertNotNull(dbUser);
    }

}
