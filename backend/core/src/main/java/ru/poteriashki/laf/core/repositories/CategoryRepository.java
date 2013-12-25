package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, String> {

}
