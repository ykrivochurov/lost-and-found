package ru.eastbanctech.board.util;

import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.CommitteeStatus;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.CompanyStatus;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 15:04
 */
public class TestHelper {

    public static User createUser() {
        User user = new User();
        user.setFirstName("John");
        user.setMiddleName("A");
        user.setLastName("Doe");
        user.setLogin("jad" + System.currentTimeMillis());
        user.setPassword("test");
        user.setPhone("+7 (777) 11-22-3333");
        user.setEmail("test@email.com");

        return user;
    }

    public static Company createCompany() {
        Company company = new Company();
        company.setStatus(CompanyStatus.ACTIVE);
        company.setName("Test " + System.currentTimeMillis());
        return company;
    }

    public static Committee createCommittee(Company company) {
        Committee committee = new Committee();
        committee.setCompany(company);
        committee.setStatus(CommitteeStatus.ACTIVE);
        committee.setName("Committee " + System.currentTimeMillis());
        return committee;
    }

    public static UserPositionInCompany createPosition(User user, Company company) {
        UserPositionInCompany position = new UserPositionInCompany();
        position.setPosition("testPosition" + System.currentTimeMillis());
        position.setUser(user);
        position.setCompany(company);
        return position;
    }
}
