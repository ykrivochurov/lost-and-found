package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Favorite;

public interface FavoriteRepository extends PagingAndSortingRepository<Favorite, String> {

    Favorite findOneByUserId(String userId);

}
