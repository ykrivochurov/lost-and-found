package ru.poteriashki.laf.core.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;

import java.util.Date;
import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Item, String> {

    Page<Item> findByItemTypeAndMainCategoryAndTagsAndCreationDateGreaterThanAndClosed(ItemType itemType, String mainCategory,
                                                                                       String tags, Date creationDate,
                                                                                       Pageable pageable, boolean closed);

    Page<Item> findByItemTypeAndMainCategoryAndCreationDateGreaterThanAndClosed(ItemType itemType, String mainCategory,
                                                                                Date creationDate, Pageable pageable, boolean closed);

    Page<Item> findByItemTypeAndMainCategoryAndCityIdAndClosed(ItemType itemType, String category, String cityId, Pageable pageable, boolean closed);

    Page<Item> findByItemTypeAndMainCategoryAndTagsAndCityIdAndClosed(ItemType itemType, String category, String tag, String cityId, Pageable pageable, boolean closed);

    Page<Item> findByAuthor(String author, Pageable pageable);

    Long countByAuthorAndClosed(String author, boolean closed);

    List<Item> findByItemTypeAndCityIdAndClosed(ItemType itemType, String cityId, boolean closed);

    Page<Item> findByClosed(Pageable pageable, boolean closed);

    Item findOneByNumber(Integer number);
}