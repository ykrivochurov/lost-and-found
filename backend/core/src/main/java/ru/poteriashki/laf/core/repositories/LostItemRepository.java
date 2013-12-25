package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.LostItem;

public interface LostItemRepository extends PagingAndSortingRepository<LostItem, String> {
}
