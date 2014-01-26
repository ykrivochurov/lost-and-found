package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.model.UserType;

public interface UserRepository extends PagingAndSortingRepository<User, String> {

    User findOneByUidAndType(String uid, UserType userType);

}