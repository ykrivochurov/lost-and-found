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
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.repositories.CategoryRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ContextConfiguration(classes = {TestPropHolderConfig.class, PersistenceMongoConfig.class, BaseConfiguration.class})
public class CategoryRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void before() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("id").exists(true)), Category.COLLECTION);
    }

    @Test
    public void testCreate() throws Exception {
        List<String> tags = new ArrayList<>();
        tags.add("Паспорт");
        tags.add("Снилс");

        Category category = new Category();
        category.setName("Документы");
        category.setTags(tags);

        categoryRepository.save(category);

        Assert.assertNotNull(category.getId());
    }

    @Test
    public void testLoad() throws Exception {
        List<String> tags = new ArrayList<>();
        tags.add("Паспорт");
        tags.add("Снилс");

        Category category = new Category();
        category.setName("Документы");
        category.setTags(tags);

        categoryRepository.save(category);

        Assert.assertNotNull(category.getId());

        for (Category category1 : categoryRepository.findAll()) {
            Assert.assertNotNull(category1.getId());
            Assert.assertEquals(category1.getTags().size(), 2);
        }
    }
}
