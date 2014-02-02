package ru.poteriashki.laf.core.repositories;

import ru.poteriashki.laf.core.model.ItemType;

import java.util.Map;

public interface ICounterDao {

    Map<String, Float> countTags(ItemType itemType);

}