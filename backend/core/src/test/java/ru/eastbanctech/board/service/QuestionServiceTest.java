package ru.eastbanctech.board.service;

import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.CommitteeRepository;
import ru.eastbanctech.board.core.dao.MeetingRepository;
import ru.eastbanctech.board.core.dao.QuestionRepository;
import ru.eastbanctech.board.core.dao.ResourceRepository;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.dao.UserVisitQuestionRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.MeetingStatus;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.Resource;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserRole;
import ru.eastbanctech.board.core.model.UserVisitQuestion;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IQuestionService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.util.TestHelper;
import ru.eastbanctech.resources.config.MongoConnectionConfig;
import ru.eastbanctech.resources.config.MongoResourceServiceConfig;
import ru.eastbanctech.resources.services.IResourceService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 16:28
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class, TestPersistenceJPAConfig.class,
        MongoConnectionConfig.class, MongoResourceServiceConfig.class})
public class QuestionServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    public static final byte[] doc = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1, 8, 2, 0, 0,
            0, -112, 119, 83, -34, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0,
            9, 112, 72, 89, 115, 0, 0, 14, -61, 0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 0, 12, 73, 68, 65, 84, 24, 87, 99, 96, 96, 96, 0, 0, 0, 4, 0, 1,
            92, -51, -1, 105, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private UserVisitQuestionRepository userVisitQuestionRepository;

    @Test
    public void testCreate() throws Exception {
        User user = TestHelper.createUser();
        userRepository.save(user);

        // Create company
        Company company = createCompany();

        // Create committee
        Committee committee = createCommittee(user, company);

        Meeting meeting = createMeeting(user);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question.setMeeting(meeting);
        question = questionService.create(question, user);
        Assert.assertNotNull(question.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        User user = TestHelper.createUser();
        userRepository.save(user);

        User notAuthor = TestHelper.createUser();
        userRepository.save(notAuthor);

        Company company = createCompany();
        Committee committee = createCommittee(user, company);
        Meeting meeting = createMeeting(user);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question.setMeeting(meeting);
        question = questionService.create(question, user);

        Question updateQuestion = new Question();
        updateQuestion.setId(question.getId());
        updateQuestion.setNumber(1);
        updateQuestion.setQuestion("Test question updated");
        updateQuestion.setSolution("Test solution");
        Committee updateCommittee = new Committee();
        updateCommittee.setId(committee.getId());
        updateQuestion.setCommittee(updateCommittee);

        questionService.update(updateQuestion, user);

        Question finalQuestion = questionRepository.findOne(updateQuestion.getId());

        Assert.assertEquals(finalQuestion.getQuestion(), updateQuestion.getQuestion());
        Assert.assertEquals(finalQuestion.getSolution(), updateQuestion.getSolution());
        Assert.assertEquals(finalQuestion.getCommittee().getId(), committee.getId());

        try {
            questionService.update(updateQuestion, notAuthor);
            Assert.fail();
        } catch (ServiceException e) {
            //do nothing
        }

        notAuthor.getRoles().add(UserRole.ROLE_SECRETARY);
        questionService.update(updateQuestion, notAuthor);
    }

    @Test
    public void testUpdateNumber() throws Exception {
        User user = TestHelper.createUser();
        userRepository.save(user);

        Company company = createCompany();

        Committee committee = createCommittee(user, company);

        Meeting meeting = createMeeting(user);

        List<Question> questionList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Question question = new Question();
            question.setNumber(i + 1);
            question.setQuestion("Test question");
            question.setSolution("Test solution");
            question.setCommittee(committee);
            question.setMeeting(meeting);
            questionList.add(questionService.create(question, user));
        }

        Question question = questionList.get(2);
        Question updateQuestion = new Question();
        updateQuestion.setId(question.getId());
        updateQuestion.setNumber(1);
        updateQuestion.setQuestion("Test question");
        updateQuestion.setSolution("Test solution");
        updateQuestion.setCommittee(question.getCommittee());

        questionService.update(updateQuestion, user);

        Question finalQuestion = questionRepository.findOne(updateQuestion.getId());
        Assert.assertEquals(finalQuestion.getNumber(), updateQuestion.getNumber());

        List<Question> updatedQuestions = questionRepository.findByMeetingIdOrderByNumberAsc(meeting.getId());
        for (int i = 0; i < updatedQuestions.size(); i++) {
            Assert.assertEquals(updatedQuestions.get(i).getNumber(), Integer.valueOf(i + 1));
        }

        question = updatedQuestions.get(1);
        updateQuestion = new Question();
        updateQuestion.setId(question.getId());
        updateQuestion.setNumber(4);
        updateQuestion.setQuestion("Test question");
        updateQuestion.setSolution("Test solution");
        updateQuestion.setCommittee(committee);
        questionService.update(updateQuestion, user);

        updatedQuestions = questionRepository.findByMeetingIdOrderByNumberAsc(meeting.getId());
        for (int i = 0; i < updatedQuestions.size(); i++) {
            Assert.assertEquals(updatedQuestions.get(i).getNumber(), Integer.valueOf(i + 1));
        }
    }

    @Test
    public void testDelete() throws Exception {
        User user = TestHelper.createUser();
        userRepository.save(user);

        Meeting meeting = createMeeting(user);

        Company company = createCompany();
        Committee committee = createCommittee(user, company);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question.setMeeting(meeting);
        question = questionService.create(question, user);

        questionService.delete(question.getId());
        Assert.assertNull(questionRepository.findOne(question.getId()));
        Assert.assertNotNull(committeeRepository.findOne(committee.getId()));
    }

    @Test
    public void testVisitQuestion() throws Exception {
        User user = TestHelper.createUser();
        userRepository.save(user);

        Company company = createCompany();

        Committee committee = createCommittee(user, company);

        Meeting meeting = createMeeting(user);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question.setMeeting(meeting);
        question = questionService.create(question, user);
        Assert.assertNotNull(question.getId());

        questionService.userVisitQuestion(user, question);

        UserVisitQuestion userVisitQuestion = userVisitQuestionRepository.
                findByUserQuestionUserIdAndUserQuestionQuestionId(user.getId(), question.getId());
        Assert.assertNotNull(userVisitQuestion.getLastVisit());
    }

    @Test
    public void testUploadAndLoadDocument() throws Exception {
        MockMultipartFile document = new MockMultipartFile("test.doc", "test.doc", "application/msword", doc);

        User user = TestHelper.createUser();
        userRepository.save(user);

        Company company = createCompany();
        Committee committee = createCommittee(user, company);

        Meeting meeting = createMeeting(user);
        meeting.getCommittees().add(committee);
        meetingRepository.save(meeting);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question.setMeeting(meeting);
        question = questionService.create(question, user);
        Assert.assertNotNull(question.getId());

        questionService.uploadDocument(user, document, question.getId());
        Resource resourceFromQuestion = question.getResources().get(0);
        Assert.assertNotNull(resourceFromQuestion.getDocumentId());
        Resource resource = resourceRepository.findOne(resourceFromQuestion.getId());
        Assert.assertEquals(resource.getAuthor(), user);
        Assert.assertEquals(resource.getName(), "test.doc");

        MockHttpServletResponse response = new MockHttpServletResponse();
        questionService.loadDocument(user, resource.getId(), response);
        Assert.assertEquals(response.getContentAsByteArray(), doc);

        questionService.deleteDocument(resourceFromQuestion.getId());
        GridFSDBFile gridFSDBFile = resourceService.load(resourceFromQuestion.getDocumentId());
        resource = resourceRepository.findOne(resourceFromQuestion.getId());
        Assert.assertNull(gridFSDBFile);
        Assert.assertNull(resource);
    }

    private Meeting createMeeting(User user) {
        Meeting meeting = new Meeting();
        meeting.setName("");
        meeting.setAuthor(user);
        meeting.setDateAndTime(new Date());
        meeting.setCreationDate(new Date());
        meeting.setStatus(MeetingStatus.TEMP);
        meeting = meetingRepository.save(meeting);
        return meeting;
    }

    private Committee createCommittee(User user, Company company) throws ServiceException {
        // create committee
        Committee committee = TestHelper.createCommittee(company);
        committee.getUsers().add(user);
        committeeService.create(committee);
        Assert.assertNotNull(committee);
        return committee;
    }

    private Company createCompany() throws ServiceException {
        Company company = TestHelper.createCompany();
        company = companyService.create(company);
        Assert.assertNotNull(company.getId());
        return company;
    }

}
