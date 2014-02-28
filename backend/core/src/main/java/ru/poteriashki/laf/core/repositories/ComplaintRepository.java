package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Complaint;

public interface ComplaintRepository extends PagingAndSortingRepository<Complaint, String> {
}
