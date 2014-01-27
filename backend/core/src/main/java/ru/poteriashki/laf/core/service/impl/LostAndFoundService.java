package ru.poteriashki.laf.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.CategoryRepository;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.service.ILostAndFoundService;

import java.util.Date;
import java.util.Set;

@Service
public class LostAndFoundService implements ILostAndFoundService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item createItem(Item item, User user, Set<String> photoIds) {
        Assert.notNull(item);
        Assert.notEmpty(item.getTags());
        Assert.hasText(item.getWhat());
        Assert.hasText(item.getWhere());
        Assert.notNull(item.getWhen());
        Assert.notNull(user);
        item.setAuthor(user.getId());
        item.setPhotosIds(photoIds);
        item.setFinished(false);
        item.setCreationDate(new Date());
        Category category = null;
        for (String tag : item.getTags()) {
            category = categoryRepository.findOneByTags(tag);
            break;
        }
        item.setMainCategory(category != null ? category.getName() : null);
        return itemRepository.save(item);
    }

}
