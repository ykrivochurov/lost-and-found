package ru.eastbanctech.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.UserPositionInCompanyRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IUserPositionInCompanyService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.util.TestHelper;

import java.util.List;

/**
 * User: a.zhukov
 * Date: 13.06.13
 * Time: 16:59
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class,
        TestPersistenceJPAConfig.class})
public class UserPositionInCompanyServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserPositionInCompanyService userPositionInCompanyService;

    @Autowired
    private UserPositionInCompanyRepository userPositionInCompanyRepository;

    @Test
    public void testCreate() throws Exception {
        User user = userService.createOrUpdate(TestHelper.createUser());
        Company company = companyService.create(TestHelper.createCompany());

        UserPositionInCompany position = TestHelper.createPosition(user, company);
        userPositionInCompanyService.create(position);
        UserPositionInCompany thePosition = userPositionInCompanyRepository.findByUserCompanyUserIdAndUserCompanyCompanyId(
                user.getId(), company.getId());

        Assert.assertEquals(thePosition.getPosition(), position.getPosition());
        Assert.assertEquals(thePosition.getUser().getId(), user.getId());
        Assert.assertEquals(thePosition.getCompany().getId(), company.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        User user = userService.createOrUpdate(TestHelper.createUser());
        Company company = companyService.create(TestHelper.createCompany());

        UserPositionInCompany position1 = TestHelper.createPosition(user, company);
        UserPositionInCompany position2 = TestHelper.createPosition(user, company);
        userPositionInCompanyService.create(position1);
        UserPositionInCompany thePosition2 = userPositionInCompanyService.update(position2);

        Assert.assertEquals(thePosition2.getPosition(), position2.getPosition());
        Assert.assertEquals(thePosition2.getUser().getId(), user.getId());
        Assert.assertEquals(thePosition2.getCompany().getId(), company.getId());
    }

    @Test
    public void testDelete() throws Exception {
        User user = userService.createOrUpdate(TestHelper.createUser());
        Company company = companyService.create(TestHelper.createCompany());

        UserPositionInCompany position = TestHelper.createPosition(user, company);
        userPositionInCompanyService.create(position);
        userPositionInCompanyService.delete(company.getId(), user.getId());
        position = userPositionInCompanyRepository.findByUserCompanyUserIdAndUserCompanyCompanyId(
                user.getId(), company.getId());

        Assert.assertNull(position);
    }

    @Test
    public void testFindByCompanyId() throws Exception {
        User user1 = userService.createOrUpdate(TestHelper.createUser());
        User user2 = TestHelper.createUser();
        user2.setLogin("anotherLogin");
        user2 = userService.createOrUpdate(user2);
        Company company1 = companyService.create(TestHelper.createCompany());
        Company company2 = companyService.create(TestHelper.createCompany());

        UserPositionInCompany position11 = TestHelper.createPosition(user1, company1);
        UserPositionInCompany position12 = TestHelper.createPosition(user1, company2);
        UserPositionInCompany position22 = TestHelper.createPosition(user2, company2);
        userPositionInCompanyService.create(position11);
        userPositionInCompanyService.create(position12);
        userPositionInCompanyService.create(position22);

        List<UserPositionInCompany> userPositionsInCompany =
                userPositionInCompanyService.findByCompanyId(company1.getId(), 0, 10).getContent();
        Assert.assertEquals(userPositionsInCompany.size(), 1);
        UserPositionInCompany thePosition11 = userPositionsInCompany.get(0);
        Assert.assertEquals(thePosition11.getPosition(), position11.getPosition());
        Assert.assertEquals(thePosition11.getUser().getId(), user1.getId());
        Assert.assertEquals(thePosition11.getCompany().getId(), company1.getId());

        userPositionsInCompany = userPositionInCompanyService.findByCompanyId(company2.getId(), 0, 1).getContent();
        Assert.assertEquals(userPositionsInCompany.size(), 1);
        UserPositionInCompany thePosition12 = userPositionsInCompany.get(0);
        Assert.assertEquals(thePosition12.getPosition(), position12.getPosition());
        Assert.assertEquals(thePosition12.getUser().getId(), user1.getId());
        Assert.assertEquals(thePosition12.getCompany().getId(), company2.getId());

        userPositionsInCompany = userPositionInCompanyService.findByCompanyId(company2.getId(), 0, 10).getContent();
        Assert.assertEquals(userPositionsInCompany.size(), 2);
        thePosition12 = userPositionsInCompany.get(0);
        Assert.assertEquals(thePosition12.getPosition(), position12.getPosition());
        Assert.assertEquals(thePosition12.getUser().getId(), user1.getId());
        Assert.assertEquals(thePosition12.getCompany().getId(), company2.getId());

        UserPositionInCompany thePosition22 = userPositionsInCompany.get(1);
        Assert.assertEquals(thePosition22.getPosition(), position22.getPosition());
        Assert.assertEquals(thePosition22.getUser().getId(), user2.getId());
        Assert.assertEquals(thePosition22.getCompany().getId(), company2.getId());

        userPositionsInCompany = userPositionInCompanyService.findByCompanyId(company2.getId(), 1, 1).getContent();
        Assert.assertEquals(userPositionsInCompany.size(), 1);
        thePosition22 = userPositionsInCompany.get(0);
        Assert.assertEquals(thePosition22.getPosition(), position22.getPosition());
        Assert.assertEquals(thePosition22.getUser().getId(), user2.getId());
        Assert.assertEquals(thePosition22.getCompany().getId(), company2.getId());
    }

    @Test
    public void testFindByCommitteeId() throws Exception {
        Company company = companyService.create(TestHelper.createCompany());
        User user = userService.create(TestHelper.createUser());
        UserPositionInCompany position = userPositionInCompanyService.create(TestHelper.createPosition(user, company));
        Committee committee = committeeService.create(TestHelper.createCommittee(company));
        Committee committee1 = committeeService.create(TestHelper.createCommittee(company));
        committeeService.addUser(user.getId(), committee.getId());
        List<UserPositionInCompany> positions = userPositionInCompanyService.findByCommitteeId(
                committee.getId(), 0, 10).getContent();
        Assert.assertEquals(positions.size(), 1);
        positions = userPositionInCompanyService.findByCommitteeId(
                committee1.getId(), 0, 10).getContent();
        Assert.assertEquals(positions.size(), 0);
    }
}
