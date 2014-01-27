package ru.poteriashki.laf.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.service.ILostAndFoundService;

import java.util.Set;

@Service
public class LostAndFoundService implements ILostAndFoundService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item createItem(Item item, User user, Set<String> photoIds) {
        item.setAuthor(user);
        item.setPhotosIds(photoIds);
        item.setFinished(false);
        return itemRepository.save(item);
    }

}
