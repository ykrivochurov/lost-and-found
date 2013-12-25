package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.FoundItem;

public interface FoundItemRepository extends PagingAndSortingRepository<FoundItem, String> {
}
