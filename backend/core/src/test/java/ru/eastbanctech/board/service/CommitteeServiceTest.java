package ru.eastbanctech.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.CommitteeStatus;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IMeetingService;
import ru.eastbanctech.board.core.service.IUserPositionInCompanyService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.util.TestHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: y.krivochurov
 * Date: 04.05.13
 * Time: 16:53
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class, TestPersistenceJPAConfig.class})
public class CommitteeServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserPositionInCompanyService userPositionInCompanyService;

    @Autowired
    private IMeetingService meetingService;

    @Test
    public void testCreate() throws Exception {
        Committee committee = getCommittee();
        Assert.assertNotNull(committee.getId());
    }

    @Test
    public void testAddUser() throws Exception {
        Committee committee = getCommittee();
        Assert.assertNotNull(committee.getId());

        User user = userService.create(TestHelper.createUser());

        UserPositionInCompany position = TestHelper.createPosition(user, committee.getCompany());
        userPositionInCompanyService.create(position);

        committeeService.addUser(user.getId(), committee.getId());
        committee = committeeService.loadOne(committee.getId());

        Assert.assertEquals(committee.getUsers().size(), 1);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void testExceptionWhenAddUserWithNoPosition() throws Exception {
        Committee committee = getCommittee();
        Assert.assertNotNull(committee.getId());

        User user = userService.create(TestHelper.createUser());

        committeeService.addUser(user.getId(), committee.getId());
    }

    @Test
    public void testDeleteUser() throws Exception {
        Committee committee = getCommittee();
        Assert.assertNotNull(committee.getId());

        User user = userService.create(TestHelper.createUser());

        UserPositionInCompany position = TestHelper.createPosition(user, committee.getCompany());
        userPositionInCompanyService.create(position);

        committeeService.addUser(user.getId(), committee.getId());
        committee = committeeService.loadOne(committee.getId());

        Assert.assertEquals(committee.getUsers().size(), 1);

        committeeService.deleteUser(user.getId(), committee.getId());
        committee = committeeService.loadOne(committee.getId());

        Assert.assertEquals(committee.getUsers().size(), 0);
    }

    @Test
    public void testFindByCompany() throws Exception {
        Committee committee = getCommittee();
        Assert.assertNotNull(committee.getId());

        List<Committee> committees = committeeService.findByCompany(committee.getCompany().getId());
        Assert.assertEquals(committees.size(), 1);
    }

    @Test
    public void testUpdate() throws Exception {
        Committee committee = getCommittee();
        Assert.assertNotNull(committee.getId());

        String name = "New name";
        committee.setName(name);
        committeeService.update(committee);
        committee = committeeService.loadOne(committee.getId());
        Assert.assertEquals(committee.getName(), name);
    }

    private Committee getCommittee() throws ServiceException {
        Company company = TestHelper.createCompany();
        company = companyService.create(company);

        Committee committee = TestHelper.createCommittee(company);
        committee.setCompany(company);

        committee = committeeService.create(committee);
        return committee;
    }

    @Test(expectedExceptions = ServiceException.class)
    public void should_throw_serviceException_when_archiving_non_empty_committee() throws ServiceException {
        Committee committee = getCommittee();

        User user = userService.create(TestHelper.createUser());

        Meeting meeting = meetingService.createTemp(user, committee.getCompany().getId());

        meeting.setDateAndTime(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));
        meeting.setCommittees(Arrays.asList(committee));
        meetingService.update(meeting);

        committeeService.changeStatus(committee.getId(), CommitteeStatus.ARCHIVE);
    }

    @Test
    public void testDelete() throws ServiceException {
        Committee committee = getCommittee();
        User user = userService.create(TestHelper.createUser());
        committee.getUsers().add(user);
        committeeService.update(committee);
        committeeService.delete(committee.getId());
        user = userService.loadOne(user.getId());
        Assert.assertNotNull(user);
        Assert.assertNull(user.getCommittees());
        boolean isOk = false;
        try {
            committeeService.loadOne(committee.getId());
        } catch (ServiceException e) {
            isOk = true;
        }
        Assert.assertEquals(isOk, true);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void testExceptionWhenDeleteCommitteeWithMeetings() throws ServiceException {
        Committee committee = getCommittee();

        User user = userService.create(TestHelper.createUser());

        Meeting meeting = meetingService.createTemp(user, committee.getCompany().getId());

        meeting.setCommittees(Arrays.asList(committee));
        meetingService.update(meeting);

        committeeService.delete(committee.getId());
    }

    @Test
    public void testFindOne() throws ServiceException {
        Committee committee = getCommittee();
        committee = committeeService.loadOne(committee.getId());
        Assert.assertNotNull(committee.getId());
        Assert.assertEquals(committee.isCanArchive(), true);
        Assert.assertEquals(committee.isCanDelete(), true);
    }

    @Test
    public void testCanDelete() throws ServiceException {
        Committee committee = getCommittee();
        Assert.assertEquals(committeeService.canArchive(committee.getId()), true);
        Assert.assertEquals(committeeService.canDelete(committee.getId()), true);

        User user = userService.create(TestHelper.createUser());

        Meeting meeting = meetingService.createTemp(user, committee.getCompany().getId());

        meeting.setDateAndTime(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
        meeting.setCommittees(Arrays.asList(committee));
        meetingService.update(meeting);

        Assert.assertEquals(committeeService.canArchive(committee.getId()), true);
        Assert.assertEquals(committeeService.canDelete(committee.getId()), false);

        meeting.setDateAndTime(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));
        meetingService.update(meeting);

        Assert.assertEquals(committeeService.canArchive(committee.getId()), false);
        Assert.assertEquals(committeeService.canDelete(committee.getId()), false);
    }
}
