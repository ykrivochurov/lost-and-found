package ru.poteriashki.laf.repository;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.poteriashki.laf.config.TestPropHolderConfig;
import ru.poteriashki.laf.core.config.BaseConfiguration;
import ru.poteriashki.laf.core.config.PersistenceMongoConfig;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.UserRepository;

@ContextConfiguration(classes = {TestPropHolderConfig.class, PersistenceMongoConfig.class, BaseConfiguration.class})
public class UserRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void before() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("id").exists(true)), User.COLLECTION);
    }

    @Test
    public void testCreate() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhone("+7 (777) 11-22-3333");
        user.setEmail("test@email.com");
        user.setLogin("test@email.com");
        user.setPassword("pwd");

        user = userRepository.save(user);

        Assert.assertNotNull(user.getId());
    }

    @Test
    public void testGet() throws Exception {
        for (User user : userRepository.findAll()) {
            Assert.assertNotNull(user.getId());
        }
    }
}