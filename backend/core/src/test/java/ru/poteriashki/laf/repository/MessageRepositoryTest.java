package ru.poteriashki.laf.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.eastbanctech.resources.config.MongoConnectionConfig;
import ru.eastbanctech.resources.config.MongoResourceServiceConfig;
import ru.poteriashki.laf.config.TestPropHolderConfig;
import ru.poteriashki.laf.core.config.BaseConfiguration;
import ru.poteriashki.laf.core.config.PersistenceMongoConfig;
import ru.poteriashki.laf.core.model.Message;
import ru.poteriashki.laf.core.repositories.MessageRepository;

import java.util.Date;
import java.util.List;

@ContextConfiguration(classes = {TestPropHolderConfig.class, PersistenceMongoConfig.class, BaseConfiguration.class, MongoConnectionConfig.class, MongoResourceServiceConfig.class})
public class MessageRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeMethod
    public void before() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("id").exists(true)), Message.class);
    }

    @Test
    public void testBase() throws Exception {
        Message message1 = new Message();
        message1.setSender("s1");
        message1.setCreationDate(new Date());
        message1.setItemId("1");
        message1.setReceiverNew(true);
        message1.setSenderNew(true);
        message1.setText("from s1 to r1");
        messageRepository.save(message1);

        Message message2 = new Message();
        message2.setSender("r1");
        message2.setCreationDate(new Date());
        message2.setItemId("1");
        message2.setReceiverNew(true);
        message2.setSenderNew(true);
        message2.setText("from r1 to s1");
        messageRepository.save(message2);

        Message message3 = new Message();
        message3.setSender("r1");
        message3.setCreationDate(new Date());
        message3.setItemId("1");
        message3.setReceiverNew(true);
        message3.setSenderNew(true);
        message3.setText("from r1 to s2");
        messageRepository.save(message3);

//        List<Message> messages = messageRepository.findByItemIdAndSenderOrReceiver("1", "s1", "s1", new Sort(Sort.Direction.DESC, "creationDate"));
//        Assert.assertEquals(messages.size(), 3);
    }
}
