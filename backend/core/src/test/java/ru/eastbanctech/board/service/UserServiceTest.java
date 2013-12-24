package ru.eastbanctech.board.service;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IUserPositionInCompanyService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.util.TestHelper;

import java.util.List;

/**
 * User: a.zhukov
 * Date: 01.07.13
 * Time: 17:44
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class,
        TestPersistenceJPAConfig.class})
public class UserServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserPositionInCompanyService userPositionInCompanyService;

    @Test(expectedExceptions = ServiceException.class)
    public void testCreate() throws ServiceException {
        User user = userService.create(TestHelper.createUser());
        user = userService.loadOne(user.getId());
        userService.create(user);
    }

    @Test
    public void testCreateOrUpdate() throws ServiceException {
        User user = userService.createOrUpdate(TestHelper.createUser());
        user = userService.loadOne(user.getId());
        String tmp = "42";
        user.setFirstName(tmp);
        userService.createOrUpdate(user);
        user = userService.loadOne(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getFirstName(), tmp);
    }

    @Test
    public void testGetByCompanyNotIn() throws ServiceException {
        User user1 = userService.create(TestHelper.createUser());
        Company company1 = companyService.create(TestHelper.createCompany());
        userPositionInCompanyService.create(TestHelper.createPosition(user1, company1));

        User user2 = userService.create(TestHelper.createUser());
        Company company2 = companyService.create(TestHelper.createCompany());
        userPositionInCompanyService.create(TestHelper.createPosition(user2, company2));

        User user3 = userService.create(TestHelper.createUser());
        userPositionInCompanyService.create(TestHelper.createPosition(user3, company1));
        userPositionInCompanyService.create(TestHelper.createPosition(user3, company2));

        User user4 = userService.create(TestHelper.createUser());

        List<User> users = userService.getByCompanyNotIn(company1.getId());
        Assert.assertEquals(users.size(), 2);
        Assert.assertEquals(users.get(0), user2);
        Assert.assertEquals(users.get(1), user4);

        users = userService.getByCompanyNotIn(company2.getId());
        Assert.assertEquals(users.size(), 2);
        Assert.assertEquals(users.get(0), user1);
        Assert.assertEquals(users.get(1), user4);
    }

    @Test
    public void testGetByCompanyInAndAllCompanyCommitteesNotIn() throws ServiceException {
        Company company1 = companyService.create(TestHelper.createCompany());
        Company company2 = companyService.create(TestHelper.createCompany());
        Committee committee1 = committeeService.create(TestHelper.createCommittee(company1));
        Committee committee2 = committeeService.create(TestHelper.createCommittee(company1));
        User user1 = userService.create(TestHelper.createUser());
        User user2 = userService.create(TestHelper.createUser());
        User user3 = userService.create(TestHelper.createUser());
        User user4 = userService.create(TestHelper.createUser());
        User user5 = userService.create(TestHelper.createUser());
        userPositionInCompanyService.create(TestHelper.createPosition(user1, company1));
        userPositionInCompanyService.create(TestHelper.createPosition(user2, company1));
        userPositionInCompanyService.create(TestHelper.createPosition(user3, company1));
        userPositionInCompanyService.create(TestHelper.createPosition(user4, company2));
        committee1.getUsers().add(user1);
        committee2.getUsers().add(user2);
        committeeService.update(committee1);
        committeeService.update(committee2);

        List<User> users = userService.getByCompanyInAndAllCompanyCommitteesNotIn(company1.getId());
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.get(0), user3);

        users = userService.getByCompanyInAndAllCompanyCommitteesNotIn(company2.getId());
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.get(0), user4);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void testLoadOne() throws ServiceException {
        User user = userService.create(TestHelper.createUser());
        user = userService.loadOne(user.getId());
        Assert.assertNotNull(user);
        user = userService.loadOne(Long.MAX_VALUE);
    }
}
