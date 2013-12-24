package ru.eastbanctech.board.core.dao;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select user from User user join user.positions position where position.userCompany.company = ?1")
    List<User> findByCompany(Company company);

    User findByLogin(String login);
}
