package ru.poteriashki.laf.core.service;

import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.User;

import java.util.Map;
import java.util.Set;

public interface ILostAndFoundService {

    Item createItem(Item item, User user, Set<String> photoIds);

    Map<String, Float> getCountsByTags();

    Iterable<Category> getAllCategories();
}
