package ru.poteriashki.laf.core.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;

import java.util.Date;
import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Item, String> {

    Page<Item> findByItemTypeAndMainCategoryAndTagsAndCreationDateGreaterThan(ItemType itemType, String mainCategory,
                                                                              String tags, Date creationDate,
                                                                              Pageable pageable);

    Page<Item> findByItemTypeAndMainCategoryAndCreationDateGreaterThan(ItemType itemType, String mainCategory,
                                                                       Date creationDate, Pageable pageable);

    Page<Item> findByItemTypeAndMainCategoryAndCityId(ItemType itemType, String category, String cityId, Pageable pageable);

    Page<Item> findByItemTypeAndMainCategoryAndTagsAndCityId(ItemType itemType, String category, String tag, String cityId, Pageable pageable);

    List<Item> findByItemTypeAndCityId(ItemType itemType, String cityId);
}