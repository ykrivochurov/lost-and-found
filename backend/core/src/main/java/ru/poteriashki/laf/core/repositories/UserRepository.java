package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, String> {
}
