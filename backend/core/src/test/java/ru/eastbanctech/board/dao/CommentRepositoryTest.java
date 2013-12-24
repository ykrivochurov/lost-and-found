package ru.eastbanctech.board.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.CommentRepository;
import ru.eastbanctech.board.core.dao.MeetingRepository;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.Comment;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.MeetingStatus;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IQuestionService;
import ru.eastbanctech.board.util.TestHelper;

import java.util.Date;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 17:29
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class, TestPersistenceJPAConfig.class})
public class CommentRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testCreate() throws Exception {
        User user = TestHelper.createUser();
        userRepository.save(user);

        // Create company
        Company company = TestHelper.createCompany();
        company = companyService.create(company);

        // Create committee
        Committee committee = TestHelper.createCommittee(company);
        committee.getUsers().add(user);
        committee = committeeService.create(committee);

        Meeting meeting = new Meeting();
        meeting.setName("Test meeting");
        meeting.setAuthor(user);
        meeting.setDateAndTime(new Date());
        meeting.setCreationDate(new Date());
        meeting.setStatus(MeetingStatus.TEMP);
        meetingRepository.save(meeting);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question.setMeeting(meeting);
        question = questionService.create(question, user);

        Comment comment = new Comment();
        comment.setText("Test comment create");
        comment.setAuthor(user);
        comment.setCreationDate(new Date());
        comment.setQuestion(question);

        commentRepository.save(comment);
        Assert.assertNotNull(comment.getId());
    }
/*  todo: useless?
    @Test
    public void testLastUnread() throws Exception {
        User user = TestHelper.createUser();
        userRepository.save(user);

        // Create company
        Company company = TestHelper.createCompany();
        company = companyService.create(company);

        // Create committee
        Committee committee = TestHelper.createCommittee(company);
        committee.getUsers().add(user);
        committee = committeeService.create(committee);

        // Create create meeting with committee
        Meeting meeting = new Meeting();
        meeting.setName("Test meeting");
        meeting.setAuthor(user);
        meeting.setDateAndTime(new Date());
        meeting.setCreationDate(new Date());
        meeting.setStatus(MeetingStatus.TEMP);
        meeting.getCommittees().add(committee);
        meetingRepository.save(meeting);

        // Create meeting w/o committee
        Meeting meetingWOCommittee = new Meeting();
        meetingWOCommittee.setName("meetingWOCommittee");
        meetingWOCommittee.setAuthor(user);
        meetingWOCommittee.setDateAndTime(new Date());
        meetingWOCommittee.setCreationDate(new Date());
        meetingWOCommittee.setStatus(MeetingStatus.TEMP);
        meetingRepository.save(meetingWOCommittee);

        // Get meetings for user and committee
        Page<Meeting> userMeetings = meetingRepository.findByUser(user, new PageRequest(0, 10));
        Assert.assertEquals(userMeetings.getContent().size(), 0);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question = questionService.create(user, meeting.getId(), question);

        Page<Question> userQuestions = questionRepository.findQuestionsByUser(user, new PageRequest(0, 10));
        Assert.assertEquals(userQuestions.getContent().size(), 1);

        Comment comment = new Comment();
        comment.setText("Test comment past");
        comment.setAuthor(user);
        comment.setCreationDate(new Date(System.currentTimeMillis() - 1000));
        comment.setQuestion(question);
        commentRepository.save(comment);

        questionService.userVisitQuestion(user, question);
        UserVisitQuestion userVisitQuestion = userVisitQuestionRepository.
                findByUserQuestionUserIdAndUserQuestionQuestionId(user.getId(), question.getId());
        Assert.assertNotNull(userVisitQuestion);

        Comment commentLast = new Comment();
        commentLast.setText("Test comment last");
        commentLast.setAuthor(user);
        commentLast.setCreationDate(new Date(System.currentTimeMillis() + 1000));
        commentLast.setQuestion(question);
        commentRepository.save(commentLast);

        Page<Comment> comments = commentRepository
                .lastUnread(user, new PageRequest(0, 10));
        Assert.assertEquals(comments.getContent().size(), 1);
        Assert.assertEquals(comments.getContent().get(0).getText(), "Test comment last");
    }

    @Test
    public void testLastUnreadSecretary() throws Exception {
        User secretary = TestHelper.createUser();
        userRepository.save(secretary);

        User commentCreator = TestHelper.createUser();
        userRepository.save(commentCreator);

        User user = TestHelper.createUser();
        userRepository.save(user);

        // Create company
        Company company = TestHelper.createCompany();
        company = companyService.create(company);

        // Create committee
        Committee committee = TestHelper.createCommittee(company);
        committee.getUsers().add(user);
        committee = committeeService.create(committee);

        // Create create meeting with committee
        Meeting meeting = new Meeting();
        meeting.setName("Test meeting");
        meeting.setAuthor(secretary);
        meeting.setDateAndTime(new Date());
        meeting.setCreationDate(new Date());
        meeting.setStatus(MeetingStatus.TEMP);
        meetingRepository.save(meeting);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question = questionService.create(secretary, meeting.getId(), question);


        // 1. Secretary didn't visit question
        Comment comment = new Comment();
        comment.setText("Test comment first");
        comment.setAuthor(commentCreator);
        comment.setCreationDate(new Date());
        comment.setQuestion(question);
        commentRepository.save(comment);

        Page<Comment> comments = commentRepository.lastUnreadInAllMeetings(secretary,
                new PageRequest(0, 10, new Sort(Sort.Direction.DESC, Comment.DATE_FIELD)));
        Assert.assertEquals(comments.getContent().size(), 1);
        Assert.assertEquals(comments.getContent().get(0).getText(), comment.getText());

        // 2. Secretary visit question
        questionService.userVisitQuestion(secretary, question);

        comments = commentRepository.lastUnreadInAllMeetings(secretary,
                new PageRequest(0, 10, new Sort(Sort.Direction.DESC, Comment.DATE_FIELD)));
        Assert.assertEquals(comments.getContent().size(), 0);

        // 3. New comment after visit
        Comment commentLast = new Comment();
        commentLast.setText("Test comment last");
        commentLast.setAuthor(commentCreator);
        commentLast.setCreationDate(new Date());
        commentLast.setQuestion(question);
        commentRepository.save(commentLast);

        comments = commentRepository.lastUnreadInAllMeetings(secretary,
                new PageRequest(0, 10, new Sort(Sort.Direction.DESC, Comment.DATE_FIELD)));
        Assert.assertEquals(comments.getContent().size(), 1);
        Assert.assertEquals(comments.getContent().get(0).getText(), commentLast.getText());
    }*/

    @Test
    public void     testCountsOfUnread() throws Exception {
        User secretary = TestHelper.createUser();
        userRepository.save(secretary);

        User commentCreator = TestHelper.createUser();
        userRepository.save(commentCreator );

        Company company = TestHelper.createCompany();
        companyService.create(company);

        Committee committee = TestHelper.createCommittee(company);
        committeeService.create(committee);

        // Create create meeting with committee
        Meeting meeting = new Meeting();
        meeting.setName("Test meeting");
        meeting.setAuthor(secretary);
        meeting.setDateAndTime(new Date());
        meeting.setCreationDate(new Date());
        meeting.setStatus(MeetingStatus.TEMP);
        meetingRepository.save(meeting);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Test question");
        question.setSolution("Test solution");
        question.setCommittee(committee);
        question.setMeeting(meeting);
        question = questionService.create(question, secretary);

        // 1. Secretary didn't visit question
        Comment comment1 = new Comment();
        comment1.setText("Test comment first_1");
        comment1.setAuthor(commentCreator);
        comment1.setCreationDate(new Date());
        comment1.setQuestion(question);
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Test comment first_2");
        comment2.setAuthor(commentCreator);
        comment2.setCreationDate(new Date());
        comment2.setQuestion(question);
        commentRepository.save(comment2);

        Page<Object[]> comments = commentRepository.countsOfUnreadInAllMeetings(secretary,
                new PageRequest(0, 10));
        Assert.assertEquals(comments.getContent().size(), 1);
        Assert.assertEquals(comments.getContent().get(0)[1], 2l);

        // 2. Secretary visit question
        questionService.userVisitQuestion(secretary, question);

        comments = commentRepository.countsOfUnreadInAllMeetings(secretary,
                new PageRequest(0, 10));
        Assert.assertEquals(comments.getContent().size(), 0);

        // 3. New comment after visit
        Comment commentLast = new Comment();
        commentLast.setText("Test comment last");
        commentLast.setAuthor(commentCreator);
        commentLast.setCreationDate(new Date());
        commentLast.setQuestion(question);
        commentRepository.save(commentLast);

        comments = commentRepository.countsOfUnreadInAllMeetings(secretary,
                new PageRequest(0, 10));
        Assert.assertEquals(comments.getContent().size(), 1);
        Assert.assertEquals(comments.getContent().get(0)[1], 1l);
    }
}
