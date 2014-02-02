package ru.poteriashki.laf.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.CategoryRepository;
import ru.poteriashki.laf.core.repositories.ICounterDao;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.service.ILostAndFoundService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class LostAndFoundService implements ILostAndFoundService {

    @Autowired
    private ICounterDao counterDao;

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

    @Override
    public Map<String, Float> getCountsByTags(ItemType itemType) {
        return counterDao.countTags(itemType);
    }

    @Override
    public Iterable<Category> getAllCategories() {
        return categoryRepository.findAll(new Sort(Sort.Direction.ASC, "priority"));
    }

    @Override
    public List<Item> getItemsForMarkers(ItemType itemType, String cityId) {
        Assert.notNull(itemType);
        Assert.notNull(cityId);

        return itemRepository.findByItemTypeAndCityId(itemType, cityId);
    }

    @Override
    public Page<Item> getItems(ItemType itemType, String category, String tag, String cityId,
                               Integer pageNumber, Integer pageSize) {
        Assert.notNull(itemType);
        Assert.hasText(category);
        Assert.notNull(cityId);
        Assert.notNull(pageNumber);
        Assert.notNull(pageSize);

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "creationDate"));

        if (StringUtils.isBlank(tag)) {
            return itemRepository.findByItemTypeAndMainCategoryAndCityId(itemType, category, cityId, pageable);
        } else {
            return itemRepository.findByItemTypeAndMainCategoryAndTagsAndCityId(itemType, category, tag, cityId, pageable);
        }
    }

    private Date getAvailableDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(60));
        return calendar.getTime();
    }
}
