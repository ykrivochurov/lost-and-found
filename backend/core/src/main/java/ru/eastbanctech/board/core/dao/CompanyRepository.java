package ru.eastbanctech.board.core.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.User;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 13:21
 */
public interface CompanyRepository extends CrudRepository<Company, Long> {

    Company findByName(String name);

    @Query("select company from Company company where company.status <> 'ARCHIVE'")
    Iterable<Company> loadAllActiveCompanies();

    @Query("select company from Company company " +
            "left join company.positionInCompanies position " +
            "join position.userCompany.user user " +
            "where company.status <> 'ARCHIVE' and user = ?1")
    Iterable<Company> loadAllActiveCompaniesByUser(User user);

}
