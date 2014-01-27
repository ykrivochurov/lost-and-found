package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Item;

public interface ItemRepository extends PagingAndSortingRepository<Item, String> {
}
