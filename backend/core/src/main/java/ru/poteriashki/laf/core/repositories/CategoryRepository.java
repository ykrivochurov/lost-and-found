package ru.poteriashki.laf.core.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Category;

import java.util.List;

public interface CategoryRepository extends PagingAndSortingRepository<Category, String> {

    List<Category> findAll(Sort sort);

    Category findOneByName(String name);

    Category findOneByTags(String tags);

}
