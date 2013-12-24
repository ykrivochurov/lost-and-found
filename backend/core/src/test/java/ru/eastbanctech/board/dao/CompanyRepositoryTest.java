package ru.eastbanctech.board.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.CommitteeRepository;
import ru.eastbanctech.board.core.dao.CompanyRepository;
import ru.eastbanctech.board.core.dao.UserPositionInCompanyRepository;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;
import ru.eastbanctech.board.util.TestHelper;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 13:23
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class, TestPersistenceJPAConfig.class})
public class CompanyRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private UserPositionInCompanyRepository userPositionInCompanyRepository;

    @Test
    public void testCreate() throws Exception {
        // create user
        User user = new User();
        user.setFirstName("John");
        user.setMiddleName("A");
        user.setLastName("Doe");
        user.setLogin("jad");
        user.setPassword("test");
        user.setPhone("+7 (777) 11-22-3333");
        user.setEmail("test@email.com");

        user = userRepository.save(user);

        Assert.assertNotNull(user.getId());

        // create company
        Company company = TestHelper.createCompany();
        company = companyRepository.save(company);
        Assert.assertNotNull(company.getId());

        UserPositionInCompany userPositionInCompany = new UserPositionInCompany();
        userPositionInCompany.setPosition("Director");
        userPositionInCompany.setCompany(company);
        userPositionInCompany.setUser(user);
        userPositionInCompany = userPositionInCompanyRepository.save(userPositionInCompany);

        company.getPositionInCompanies().add(userPositionInCompany);

        companyRepository.save(company);
        company = companyRepository.findOne(company.getId());
        Assert.assertEquals(company.getPositionInCompanies().get(0).getPosition(), "Director");

        // create committee
        Committee committee = TestHelper.createCommittee(company);
        committee.getUsers().add(user);

        committee = committeeRepository.save(committee);
        Assert.assertNotNull(committee.getId());

        committee = committeeRepository.findOne(committee.getId());

        Assert.assertEquals(committee.getUsers().size(), 1);
    }

}
