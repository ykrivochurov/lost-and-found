package ru.poteriashki.laf.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ru.eastbanctech.resources.services.ImageResourceInfo;
import ru.eastbanctech.resources.services.impl.ResourceService;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.model.TempResource;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.CategoryRepository;
import ru.poteriashki.laf.core.repositories.ICounterDao;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.repositories.TempResourceRepository;
import ru.poteriashki.laf.core.service.ErrorType;
import ru.poteriashki.laf.core.service.ILostAndFoundService;
import ru.poteriashki.laf.core.service.IMessageService;
import ru.poteriashki.laf.core.service.ServiceException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LostAndFoundService implements ILostAndFoundService {

    @Autowired
    private ICounterDao counterDao;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private TempResourceRepository tempResourceRepository;

    @Autowired
    private IMessageService messageService;

    @Override
    public Item loadByNumber(Integer number, User user) throws ServiceException {
        Assert.notNull(number);

        Item item = itemRepository.findOneByNumberAndClosed(number, false);
        if (item == null) {
            return null;
        }
        if (!item.isShowPrivateInfo() &&
                (user == null || !item.getAuthor().equals(user.getId()))) {
            item.setUser(null);
        }
        return item;
    }

    @Override
    public Item createItem(Item item, User user) throws InterruptedException, IOException, ru.eastbanctech.resources.services.ServiceException {
        Assert.notNull(item);
        Assert.notEmpty(item.getTags());
        Assert.hasText(item.getWhat());
        Assert.hasText(item.getWhere());
        Assert.notNull(item.getWhen());
        Assert.notNull(user);
        item.setAuthor(user.getId());
        item.setCreationDate(new Date());
        item.setUser(user);
        Category category = null;
        for (String tag : item.getTags()) {
            category = categoryRepository.findOneByTags(tag);
            break;
        }
        item.setMainCategory(category != null ? category.getName() : null);

        if (item.getPhotoId() != null) {
            ImageResourceInfo imageResourceInfo = resourceService.saveWithAnotherSize(item.getPhotoId(), 100, 100, false);
            item.setThumbnailId(imageResourceInfo.getId());
        }

        Item savedItem = saveInternal(item);

        if (savedItem.getPhotoId() != null) {
            TempResource tempResource = tempResourceRepository.findOneByFileId(savedItem.getPhotoId());
            if (tempResource != null) {
                tempResourceRepository.delete(tempResource);
            }
        }

        return savedItem;
    }

    @Override
    public Item updateItem(Item item, User user) throws ServiceException, InterruptedException, IOException, ru.eastbanctech.resources.services.ServiceException {
        Assert.notNull(item);
        Assert.hasText(item.getId());
        Assert.notEmpty(item.getTags());
        Assert.hasText(item.getWhat());
        Assert.hasText(item.getWhere());
        Assert.notNull(item.getWhen());
        Assert.notNull(user);

        Item existingItem = itemRepository.findOne(item.getId());

        if (existingItem == null) {
            throw new ServiceException(ErrorType.DATABASE_ERROR, "Item doesn't exist id = " + item.getId());
        }

        if (!existingItem.getAuthor().equals(user.getId())) {
            throw new ServiceException(ErrorType.ACCESS_DENIED, "");
        }

        existingItem.setWhat(item.getWhat());
        existingItem.setWhere(item.getWhere());
        existingItem.setWhen(item.getWhen());
        existingItem.setLocation(item.getLocation());
        existingItem.setShowPrivateInfo(item.isShowPrivateInfo());
        existingItem.setMoney(item.isMoney());

        existingItem.getUser().setPhone(item.getUser().getPhone());
        existingItem.getUser().setEmail(item.getUser().getEmail());
        existingItem.getUser().setName(item.getUser().getName());

        existingItem.setModificationDate(new Date());

        existingItem.setPhotoId(item.getPhotoId());

        existingItem.setTags(item.getTags());

        Category category = null;
        for (String tag : existingItem.getTags()) {
            category = categoryRepository.findOneByTags(tag);
            break;
        }
        existingItem.setMainCategory(category != null ? category.getName() : null);

        if (existingItem.getPhotoId() != null) {
            ImageResourceInfo imageResourceInfo = resourceService.saveWithAnotherSize(existingItem.getPhotoId(), 100, 100, false);
            existingItem.setThumbnailId(imageResourceInfo.getId());
        }

        if (existingItem.getPhotoId() != null) {
            TempResource tempResource = tempResourceRepository.findOneByFileId(existingItem.getPhotoId());
            if (tempResource != null) {
                tempResourceRepository.delete(tempResource);
            }
        }

        return itemRepository.save(existingItem);
    }

    @Override
    public Item changeCloseStatus(String id, User user) throws ServiceException {
        Assert.notNull(user);
        Assert.hasText(id);

        Item item = itemRepository.findOne(id);
        if (item == null) {
            throw new ServiceException(ErrorType.CONFLICT, "Item not found id = " + id);
        }

        if (!item.getAuthor().equals(user.getId())) {
            throw new ServiceException(ErrorType.ACCESS_DENIED, "");
        }

        item.setClosed(!item.isClosed());
        return itemRepository.save(item);
    }

    private synchronized Item saveInternal(Item item) {
        Pageable pageable = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "number"));
        Page<Item> items = itemRepository.findAll(pageable);
        if (items.hasContent()) {
            Item prevItem = items.getContent().get(0);
            item.setNumber(prevItem.getNumber() + 1);
        } else {
            item.setNumber(1);
        }
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

        List<Item> items = itemRepository.findByItemTypeAndCityIdAndClosed(itemType, cityId, false);

        for (Item item : items) {
            if (!item.isShowPrivateInfo()) {
                item.setUser(null);
            }
        }

        return items;
    }

    @Override
    public User itemsCountToUser(User user) {
        Assert.notNull(user);

        user.setItemsCount(itemRepository.countByAuthorAndClosed(user.getId(), false));
        user.setClosedCount(itemRepository.countByAuthorAndClosed(user.getId(), true));
        return user;
    }

    @Override
    public Page<Item> getMyItems(User user, Integer pageNumber, Integer pageSize) throws ServiceException {
        Assert.notNull(user);
        Assert.notNull(pageNumber);
        Assert.notNull(pageSize);

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "closed"),
                new Sort.Order(Sort.Direction.DESC, "creationDate"));
        Pageable pageable = new PageRequest(pageNumber, pageSize, sort);
        Page<Item> byAuthor = itemRepository.findByAuthor(user.getId(), pageable);
        for (Item item : byAuthor) {
            item.setMessages(messageService.loadByItemId(item.getId(), user));
            if (!item.isShowPrivateInfo()) {
                item.setUser(null);
            }
        }
        return byAuthor;
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

        Page<Item> itemsPage;

        if (StringUtils.isBlank(tag)) {
            itemsPage = itemRepository.findByItemTypeAndMainCategoryAndCityIdAndClosed(itemType, category, cityId, pageable, false);
        } else {
            itemsPage = itemRepository.findByItemTypeAndMainCategoryAndTagsAndCityIdAndClosed(itemType, category, tag, cityId, pageable, false);
        }

        for (Item item : itemsPage) {
            if (!item.isShowPrivateInfo()) {
                item.setUser(null);
            }
        }

        return itemsPage;
    }

    @Override
    public String createPhoto(MultipartFile fileData) throws IOException, ru.eastbanctech.resources.services.ServiceException {
        String fileId = resourceService.saveMultipart(fileData);
        tempResourceRepository.save(new TempResource(fileId, new Date()));
        return fileId;
    }

    @Override
    @Scheduled(fixedRate = 86400000)
    public void cleanupUselessPhotos() throws ru.eastbanctech.resources.services.ServiceException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.HOURS.toMillis(2));
        List<TempResource> tempResources = tempResourceRepository.findByCreationDateGreaterThan(calendar.getTime());
        for (TempResource tempResource : tempResources) {
            resourceService.delete(tempResource.getFileId());
            tempResourceRepository.delete(tempResource);
        }
    }
}