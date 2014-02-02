package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.City;

public interface CityRepository extends PagingAndSortingRepository<City, String> {

    City findOneByName(String name);

}
