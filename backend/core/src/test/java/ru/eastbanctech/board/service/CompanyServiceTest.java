package ru.eastbanctech.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.CompanyRepository;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.CompanyStatus;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IMeetingService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.util.TestHelper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: y.krivochurov
 * Date: 04.05.13
 * Time: 15:50
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class,
        TestPersistenceJPAConfig.class})
public class CompanyServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private UserRepository userRepository;

    @Test(expectedExceptions = ServiceException.class)
    public void testCreate() throws Exception {
        Company company = TestHelper.createCompany();
        company = companyService.create(company);
        Assert.assertNotNull(company.getId());
        companyService.create(company);
    }

    @Test
    public void testDelete() throws Exception {
        Company company = TestHelper.createCompany();
        company = companyService.create(company);
        Assert.assertNotNull(company.getId());

        companyService.delete(company.getId());
    }

    @Test(enabled = false) // todo it's too complex to remove company with committees. meetings and ...
    public void testDeleteWithCommittee() throws Exception {
        Company company = TestHelper.createCompany();
        company = companyService.create(company);
        Assert.assertNotNull(company.getId());

        User user = TestHelper.createUser();
        user = userRepository.save(user);

        // create committee
        Committee committee = TestHelper.createCommittee(company);
        committee.getUsers().add(user);
        committeeService.create(committee);

        company = companyRepository.findOne(company.getId());
        Assert.assertEquals(company.getCommittees().size(), 1);

        companyService.delete(company.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        Company company = TestHelper.createCompany();
        company = companyService.create(company);
        Assert.assertNotNull(company.getId());

        String name = "New name";
        Company updatedCompany = new Company();
        updatedCompany.setId(company.getId());
        updatedCompany.setName(name);
        updatedCompany.setStatus(company.getStatus());
        company = companyService.update(updatedCompany);

        companyRepository.findOne(company.getId());
        Assert.assertEquals(company.getName(), name);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void testUpdateWithError() throws Exception {
        Company company = TestHelper.createCompany();
        company.setName("Test company");
        company = companyService.create(company);
        Assert.assertNotNull(company.getId());

        String name = "Test company";
        Company updatedCompany = new Company();
        updatedCompany.setId(company.getId());
        updatedCompany.setName(name);
        updatedCompany.setStatus(company.getStatus());
        companyService.update(updatedCompany);
    }

    @Test
    public void testAddUserWithPosition() throws Exception {
        User user = TestHelper.createUser();
        user = userRepository.save(user);

        Company company = TestHelper.createCompany();
        company = companyService.create(company);
        Assert.assertNotNull(company.getId());

        companyService.updateUserWithPosition(company.getId(), "CTO", user.getId());

        user = userRepository.findOne(user.getId());
        Assert.assertEquals(user.getPositions().size(), 1);
        Assert.assertEquals(user.getPositions().get(0).getPosition(), "CTO");
    }

    @Test(expectedExceptions = ServiceException.class)
    public void should_throw_serviceException_when_archiving_non_empty_company() throws ServiceException {
        Company company = companyService.create(TestHelper.createCompany());

        User user = userRepository.save(TestHelper.createUser());

        Meeting meeting = meetingService.createTemp(user, company.getId());
        meeting.setDateAndTime(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));
        meetingService.update(meeting);

        companyService.changeStatus(company.getId(), CompanyStatus.ARCHIVE);
    }

    @Test
    public void testCanArchive() throws ServiceException {
        Company company = companyService.create(TestHelper.createCompany());

        Assert.assertEquals(companyService.canArchive(company.getId()), true);

        User user = userRepository.save(TestHelper.createUser());
        Meeting meeting = meetingService.createTemp(user, company.getId());
        meeting.setDateAndTime(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
        meetingService.update(meeting);

        Assert.assertEquals(companyService.canArchive(company.getId()), true);

        meeting.setDateAndTime(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));
        meetingService.update(meeting);

        Assert.assertEquals(companyService.canArchive(company.getId()), false);
    }

    @Test
    public void testCanDelete() throws ServiceException {
        Company company = companyService.create(TestHelper.createCompany());

        Assert.assertEquals(companyService.canDelete(company.getId()), true);

        User user = userRepository.save(TestHelper.createUser());
        Meeting meeting = meetingService.createTemp(user, company.getId());
        meeting.setDateAndTime(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
        meetingService.update(meeting);

        Assert.assertEquals(companyService.canDelete(company.getId()), false);

        meeting.setDateAndTime(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));
        meetingService.update(meeting);

        Assert.assertEquals(companyService.canDelete(company.getId()), false);
    }

    @Test
    public void testLoadOne() throws ServiceException {
        Company company = companyService.create(TestHelper.createCompany());
        company = companyService.loadOne(company.getId());
        Assert.assertNotNull(company.getId());
        Assert.assertEquals(company.isCanDelete(), true);
        Assert.assertEquals(company.isCanArchive(), true);
    }

    @Test
    public void testLoadAll() throws ServiceException {
        companyService.create(TestHelper.createCompany());
        companyService.create(TestHelper.createCompany());
        companyService.create(TestHelper.createCompany());

        List<Company> companies = (List<Company>) companyService.loadAll();
        Assert.assertEquals(companies.size(), 3);
    }

    @Test
    public void testLoadActiveCompanies() throws ServiceException {
        companyService.create(TestHelper.createCompany());
        companyService.create(TestHelper.createCompany());
        Company company = companyService.create(TestHelper.createCompany());
        companyService.changeStatus(company.getId(), CompanyStatus.ARCHIVE);

        List<Company> companies = (List<Company>) companyService.loadActiveCompanies();
        Assert.assertEquals(companies.size(), 2);
    }
}
