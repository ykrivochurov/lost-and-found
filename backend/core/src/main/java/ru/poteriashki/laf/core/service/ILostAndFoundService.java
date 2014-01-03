package ru.poteriashki.laf.core.service;

import ru.poteriashki.laf.core.model.FoundItem;
import ru.poteriashki.laf.core.model.LostItem;
import ru.poteriashki.laf.core.model.User;

import java.util.Set;

public interface ILostAndFoundService {
    FoundItem createFoundItem(FoundItem foundItem, User user, Set<String> photoIds);

    LostItem createLostItem(LostItem lostItem, User user, Set<String> photoIds);
}
