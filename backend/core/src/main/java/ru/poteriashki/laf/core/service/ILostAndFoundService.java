package ru.poteriashki.laf.core.service;

import org.springframework.data.domain.Page;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ILostAndFoundService {

    Item createItem(Item item, User user, Set<String> photoIds);

    Map<String, Float> getCountsByTags(ItemType itemType);

    Iterable<Category> getAllCategories();

    Page<Item> getItems(ItemType itemType, String category, String tag, String cityId, Integer pageNumber, Integer pageSize);

    List<Item> getItemsForMarkers(ItemType itemType, String cityId);
}
