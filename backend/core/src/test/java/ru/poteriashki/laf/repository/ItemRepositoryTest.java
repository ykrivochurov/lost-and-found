package ru.poteriashki.laf.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.poteriashki.laf.config.TestPropHolderConfig;
import ru.poteriashki.laf.core.config.BaseConfiguration;
import ru.poteriashki.laf.core.config.PersistenceMongoConfig;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.repositories.ItemRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@ContextConfiguration(classes = {TestPropHolderConfig.class, PersistenceMongoConfig.class, BaseConfiguration.class})
public class ItemRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeMethod
    public void before() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("id").exists(true)), Item.COLLECTION);
    }

    @Test
    public void testSelection() throws Exception {
        int index = 0;
        itemRepository.save(getItem(ItemType.LOST, "cat1", "tag1", ++index));
        itemRepository.save(getItem(ItemType.LOST, "cat1", "tag1", ++index));
        itemRepository.save(getItem(ItemType.LOST, "cat1", "tag2", ++index));
        itemRepository.save(getItem(ItemType.FOUND, "cat1", "tag2", ++index));
        itemRepository.save(getItem(ItemType.FOUND, "cat1", "tag3", ++index));
        itemRepository.save(getItem(ItemType.FOUND, "cat1", "tag2", ++index));
        itemRepository.save(getItem(ItemType.FOUND, "cat2", "tag4", ++index));
        itemRepository.save(getItem(ItemType.FOUND, "cat2", "tag4", ++index));
        itemRepository.save(getItem(ItemType.FOUND, "cat1", "tag7", ++index));
        itemRepository.save(getItem(ItemType.FOUND, "cat1", "tag6", ++index));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(60));

        Page<Item> items = itemRepository.findByItemTypeAndMainCategoryAndTagsAndCreationDateGreaterThanAndClosed(ItemType.LOST, "cat1", "tag2",
                calendar.getTime(), new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "creationDate")), false);
        Assert.assertEquals(items.getContent().size(), 1);

        items = itemRepository.findByItemTypeAndMainCategoryAndCreationDateGreaterThanAndClosed(ItemType.LOST, "cat1",
                calendar.getTime(), new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "creationDate")), false);
        Assert.assertEquals(items.getContent().size(), 3);

        items = itemRepository.findByItemTypeAndMainCategoryAndCreationDateGreaterThanAndClosed(ItemType.FOUND, "cat1",
                calendar.getTime(), new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "creationDate")), false);
        Assert.assertEquals(items.getContent().size(), 5);
    }

    private Item getItem(ItemType itemType, String category, String tag, int index) {
        Item item1 = new Item();
        item1.setItemType(itemType);
        item1.setWhat("part" + index);
        item1.setWhen(new Date());
        item1.setWhere("place" + index);
        item1.setCreationDate(new Date());
        item1.setMainCategory(category);
        item1.getTags().add(tag);
        return item1;
    }


}
