package ru.poteriashki.laf.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.poteriashki.laf.core.model.FoundItem;
import ru.poteriashki.laf.core.model.LostItem;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.FoundItemRepository;
import ru.poteriashki.laf.core.repositories.LostItemRepository;
import ru.poteriashki.laf.core.service.ILostAndFoundService;

import java.util.Set;

@Service
public class LostAndFoundService implements ILostAndFoundService {

    @Autowired
    private FoundItemRepository foundItemRepository;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Override
    public FoundItem createFoundItem(FoundItem foundItem, User user, Set<String> photoIds) {
        foundItem.setAuthor(user);
        foundItem.setPhotosIds(photoIds);
        foundItem.setFinished(false);
        return foundItemRepository.save(foundItem);
    }

    @Override
    public LostItem createLostItem(LostItem lostItem, User user, Set<String> photoIds) {
        lostItem.setAuthor(user);
        lostItem.setPhotosIds(photoIds);
        lostItem.setFinished(false);
        return lostItemRepository.save(lostItem);
    }


}
