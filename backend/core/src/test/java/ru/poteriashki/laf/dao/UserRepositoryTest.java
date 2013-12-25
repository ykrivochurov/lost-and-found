package ru.poteriashki.laf.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.poteriashki.laf.config.TestPropHolderConfig;
import ru.poteriashki.laf.core.config.BaseConfiguration;
import ru.poteriashki.laf.core.config.PersistenceMongoConfig;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.UserRepository;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 11:49
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, PersistenceMongoConfig.class, BaseConfiguration.class})
public class UserRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreate() throws Exception {

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhone("+7 (777) 11-22-3333");
        user.setEmail("test@email.com");

        user = userRepository.save(user);

        Assert.assertNotNull(user.getId());
    }
}