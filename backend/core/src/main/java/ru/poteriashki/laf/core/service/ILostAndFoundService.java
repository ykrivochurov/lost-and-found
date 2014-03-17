package ru.poteriashki.laf.core.service;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;
import ru.eastbanctech.resources.services.ServiceException;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.model.Favorite;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ILostAndFoundService {

    Item loadByNumber(Integer number, User user) throws ru.poteriashki.laf.core.service.ServiceException;

    Item createItem(Item item, User user) throws InterruptedException, IOException, ru.eastbanctech.resources.services.ServiceException;

    Item updateItem(Item item, User user) throws InterruptedException, IOException, ServiceException, ru.poteriashki.laf.core.service.ServiceException;

    Item changeCloseStatus(String id, User user) throws ru.poteriashki.laf.core.service.ServiceException;

    Map<String, Float> getCountsByTags(ItemType itemType);

    Iterable<Category> getAllCategories();

    User itemsCountToUser(User user);

    Page<Item> getMyItems(User user, Integer pageNumber, Integer pageSize) throws ru.poteriashki.laf.core.service.ServiceException;

    Page<Item> getItems(ItemType itemType, String category, String tag, String cityId, Integer pageNumber, Integer pageSize);

    List<Item> getItemsForMarkers(ItemType itemType, String cityId);

    String createPhoto(MultipartFile fileData) throws IOException, ServiceException;

    @Scheduled(cron = "")
    void cleanupUselessPhotos() throws ru.eastbanctech.resources.services.ServiceException;

    List<Item> search(String searchString, ItemType itemType);

    Favorite favoriteOnOff(String itemId, User user);

    Favorite favoriteForUser(User user);

    Iterable<Item> favoriteItems(User user);
}
