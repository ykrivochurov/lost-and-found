package ru.poteriashki.laf.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.service.ILostAndFoundService;

import java.util.Date;
import java.util.List;

@ContextConfiguration(classes = {TestPropHolderConfig.class, PersistenceMongoConfig.class, BaseConfiguration.class, MongoConnectionConfig.class, MongoResourceServiceConfig.class})
public class LostAndFoundServiceTest extends AbstractTestNGSpringContextTests {


    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ILostAndFoundService lostAndFoundService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeMethod
    public void before() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("id").exists(true)), Item.class);
    }

    @Test
    public void testSearch() throws Exception {
        Item item1 = new Item();
        item1.setItemType(ItemType.LOST);
        item1.setWhat("domestic cat");
        item1.setWhen(new Date());
        item1.setWhere("place");
        item1.setCreationDate(new Date());
        item1.setMainCategory("test");
        item1.getTags().add("test");

        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setItemType(ItemType.LOST);
        item2.setWhat("test");
        item2.setWhen(new Date());
        item2.setWhere("at the cat street");
        item2.setCreationDate(new Date());
        item2.setMainCategory("test");
        item2.getTags().add("test");

        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setItemType(ItemType.LOST);
        item3.setWhat("test");
        item3.setWhen(new Date());
        item3.setWhere("at the dog street");
        item3.setCreationDate(new Date());
        item3.setMainCategory("test");
        item3.getTags().add("test");

        itemRepository.save(item3);

        List<Item> items = lostAndFoundService.search("cat", ItemType.LOST);
        Assert.assertEquals(items.size(), 2);
    }
}
