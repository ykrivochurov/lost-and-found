package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.TempResource;

import java.util.Date;
import java.util.List;

public interface TempResourceRepository extends PagingAndSortingRepository<TempResource, String> {

    TempResource findOneByFileId(String fileId);

    List<TempResource> findByCreationDateGreaterThan(Date creationDate);

}