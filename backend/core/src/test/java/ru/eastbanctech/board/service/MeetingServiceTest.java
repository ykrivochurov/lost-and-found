package ru.eastbanctech.board.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.MeetingRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.MeetingStatus;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IMeetingService;
import ru.eastbanctech.board.core.service.IUserPositionInCompanyService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.util.TestHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: y.krivochurov
 * Date: 25.05.13
 * Time: 13:46
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class,
        TestPersistenceJPAConfig.class})
public class MeetingServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private IUserPositionInCompanyService userPositionInCompanyService;

    @Test
    public void testCreateTemp() throws Exception {
        User user = userService.createOrUpdate(TestHelper.createUser());
        Company company = companyService.create(TestHelper.createCompany());

        Meeting meeting = meetingService.createTemp(user, company.getId());
        meeting = meetingRepository.findOne(meeting.getId());
        Assert.assertNotNull(meeting.getId());
        Assert.assertEquals(meeting.getStatus(), MeetingStatus.TEMP);
    }

    @Test
    public void testSaveDraft() throws Exception {
        User user1 = userService.createOrUpdate(TestHelper.createUser());
        User user2 = userService.createOrUpdate(TestHelper.createUser());
        Company company = companyService.create(TestHelper.createCompany());

        Meeting meeting = meetingService.createTemp(user1, company.getId());
        meeting = meetingRepository.findOne(meeting.getId());

        Assert.assertNotNull(meeting.getId());
        Assert.assertEquals(meeting.getStatus(), MeetingStatus.TEMP);

        UserPositionInCompany position = TestHelper.createPosition(user1, company);
        userPositionInCompanyService.create(position);

        // create committee
        Committee committee1 = committeeService.create(TestHelper.createCommittee(company));
        committeeService.addUser(user1.getId(), committee1.getId());
        Committee committee2 = committeeService.create(TestHelper.createCommittee(company));
        committeeService.addUser(user1.getId(), committee2.getId());

        Date dateAndTime = new Date();
        meeting.setName("Test name");
        meeting.setDateAndTime(dateAndTime);
        meeting.setAddress("Test address");
        meeting.setPersonalPresence(Boolean.TRUE);
        meeting.setStatus(MeetingStatus.DRAFT);
        meeting.setCommittees(Arrays.asList(committee1, committee2));

        meeting = meetingService.update(meeting);
        Assert.assertEquals(meeting.getName(), "Test name");
        Assert.assertEquals(meeting.getDateAndTime(), dateAndTime);
        Assert.assertEquals(meeting.getAddress(), "Test address");
        Assert.assertEquals(meeting.getPersonalPresence(), Boolean.TRUE);
        Assert.assertEquals(meeting.getStatus(), MeetingStatus.DRAFT);
        Assert.assertEquals(meeting.getCommittees().size(), 2);
    }

    @Test
    public void testFindByFilter() throws Exception {
        // create user
        User user = userService.createOrUpdate(TestHelper.createUser());

        //create company 1
        Company company = companyService.create(TestHelper.createCompany());

        UserPositionInCompany position = TestHelper.createPosition(user, company);
        userPositionInCompanyService.create(position);

        // create committee
        Committee committee = committeeService.create(TestHelper.createCommittee(company));
        committeeService.addUser(user.getId(), committee.getId());

        // create meeting
        Meeting meeting = new Meeting();
        meeting.setAuthor(user);
        meeting.setAddress("Test address");
        meeting.getCommittees().add(committee);
        meeting.setCompany(company);
        meeting.setCreationDate(new Date());
        meeting.setDateAndTime(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2)));
        meeting.setPersonalPresence(true);
        meeting.setName("Our first meeting test");
        meeting.setStatus(MeetingStatus.PUBLISHED);

        meetingRepository.save(meeting);

        List<Meeting> meetings = meetingService.findByFilter(Arrays.asList(MeetingStatus.PUBLISHED),
                Arrays.asList(company),
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2) - 1000),
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)));

        Assert.assertEquals(meetings.size(), 1);

        meetings = meetingService.findByFilterAndUser(user, Arrays.asList(MeetingStatus.PUBLISHED),
                Arrays.asList(company),
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2) - 1000),
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)));

        Assert.assertEquals(meetings.size(), 1);
    }

    @Test
    public void testFindNearest() throws Exception {
        User user1 = userService.createOrUpdate(TestHelper.createUser());
        Company company = companyService.create(TestHelper.createCompany());

        Meeting meeting = meetingService.createTemp(user1, company.getId());

        // Should be empty, because TEMP meeting should be ignored
        List<Meeting> meetings = meetingService.findNearest(2);
        Assert.assertEquals(meetings.size(), 0);

        Date dateAndTime = new Date(System.currentTimeMillis() + 60000);

        meeting.setDateAndTime(dateAndTime);
        meeting.setStatus(MeetingStatus.DRAFT);
        meeting = meetingService.update(meeting);

        Meeting meeting2 = meetingService.createTemp(user1, company.getId());
        meeting2.setDateAndTime(dateAndTime);
        meeting2.setStatus(MeetingStatus.DRAFT);
        meeting2 = meetingService.update(meeting2);

        Meeting meeting3 = meetingService.createTemp(user1, company.getId());
        meeting3.setDateAndTime(dateAndTime);
        meeting3.setStatus(MeetingStatus.DRAFT);
        meetingService.update(meeting3);

        meetings = Lists.newArrayList(meetingRepository.findAll());

        // Should be empty, because TEMP meeting should be ignored
        meetings = meetingService.findNearest(2);
        Assert.assertEquals(meetings.size(), 2);
    }

    @Test
    public void testFindNearestByUser() throws Exception {
        User user1 = userService.createOrUpdate(TestHelper.createUser());
        User user2 = userService.createOrUpdate(TestHelper.createUser());

        Company company = companyService.create(TestHelper.createCompany());

        UserPositionInCompany position = TestHelper.createPosition(user2, company);
        userPositionInCompanyService.create(position);

        Committee committee = committeeService.create(TestHelper.createCommittee(company));
        committeeService.addUser(user2.getId(), committee.getId());

        Meeting meeting = meetingService.createTemp(user1, company.getId());

        // Should be empty, because TEMP meeting should be ignored
        List<Meeting> meetings = meetingService.findNearestByUser(user2, 2);
        Assert.assertEquals(meetings.size(), 0);

        Date dateAndTime = new Date(System.currentTimeMillis() + 60000);

        meeting.setDateAndTime(dateAndTime);
        meeting.getCommittees().add(committee);
        meeting.setStatus(MeetingStatus.DRAFT);
        meeting = meetingService.update(meeting);

        Meeting meeting2 = meetingService.createTemp(user1, company.getId());
        meeting2.setDateAndTime(dateAndTime);
        meeting2.setStatus(MeetingStatus.DRAFT);
        meeting2.getCommittees().add(committee);
        meeting2 = meetingService.update(meeting2);

        Meeting meeting3 = meetingService.createTemp(user1, company.getId());
        meeting3.setDateAndTime(dateAndTime);
        meeting3.setStatus(MeetingStatus.DRAFT);
        meeting3.getCommittees().add(committee);
        meetingService.update(meeting3);

        meetings = Lists.newArrayList(meetingRepository.findAll());

        // Should be empty, because TEMP meeting should be ignored
        meetings = meetingService.findNearestByUser(user2, 2);
        Assert.assertEquals(meetings.size(), 2);
    }

}