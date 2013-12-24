package ru.eastbanctech.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.CommentRepository;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.Comment;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;
import ru.eastbanctech.board.core.service.ICommentService;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IMeetingService;
import ru.eastbanctech.board.core.service.IQuestionService;
import ru.eastbanctech.board.core.service.IUserPositionInCompanyService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.util.TestHelper;

import java.util.List;

/**
 * User: y.krivochurov
 * Date: 05.06.13
 * Time: 8:49
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class,
        TestPersistenceJPAConfig.class})
public class CommentServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IUserPositionInCompanyService userPositionInCompanyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testCreate() throws Exception {
        User user = TestHelper.createUser();
        user = userRepository.save(user);

        Question question = createQuestion(user);

        Comment comment = new Comment();
        comment.setText("It's test comment");
        comment.setQuestion(question);
        comment = commentService.create(comment, user);

        Assert.assertNotNull(comment.getId());
        Assert.assertEquals(comment.getAuthor(), user);
        Assert.assertEquals(comment.getQuestion(), question);
        Assert.assertEquals(comment.getText(), "It's test comment");
    }

    @Test
    public void testLoadByQuestion() throws Exception {
        User user = TestHelper.createUser();
        user = userRepository.save(user);

        Question question = createQuestion(user);

        Comment comment1 = new Comment();
        comment1.setText("It's test comment #1");
        comment1.setQuestion(question);
        commentService.create(comment1, user);

        Comment comment2 = new Comment();
        comment2.setText("It's test comment #2");
        comment2.setQuestion(question);
        commentService.create(comment2, user);

        List<Comment> comments = commentService.loadByQuestion(question.getId(), new PageRequest(0, 3), user);
        Assert.assertEquals(comments.size(), 2);

        for (Comment comment : comments) {
            Assert.assertTrue(comment.isNotVisited());
        }

        questionService.userVisitQuestion(user, question);

        comments = commentService.loadByQuestion(question.getId(), new PageRequest(0, 3), user);
        Assert.assertEquals(comments.size(), 2);

        for (Comment comment : comments) {
            Assert.assertFalse(comment.isNotVisited());
        }

        Comment comment3 = new Comment();
        comment3.setText("It's test comment #3");
        comment3.setQuestion(question);
        commentService.create(comment3, user);

        comments = commentService.loadByQuestion(question.getId(), new PageRequest(0, 3), user);
        Assert.assertEquals(comments.size(), 3);

        Assert.assertFalse(comments.get(0).isNotVisited());
        Assert.assertFalse(comments.get(1).isNotVisited());
        Assert.assertFalse(comments.get(2).isNotVisited());
    }

    @Test
    public void testDelete() throws Exception {
        User user = TestHelper.createUser();
        user = userRepository.save(user);

        Question question = createQuestion(user);

        Comment comment1 = new Comment();
        comment1.setText("It's test comment #1");
        comment1.setQuestion(question);
        commentService.create(comment1, user);

        Assert.assertNotNull(comment1.getId());

        commentRepository.delete(comment1.getId());

        Comment comment = commentRepository.findOne(comment1.getId());
        Assert.assertNull(comment);
    }

    private Question createQuestion(User user) throws ServiceException {
        Company company = TestHelper.createCompany();
        company = companyService.create(company);

        UserPositionInCompany userPositionInCompany = TestHelper.createPosition(user, company);
        userPositionInCompanyService.create(userPositionInCompany);

        Committee committee = TestHelper.createCommittee(company);
        committee.getUsers().add(user);
        committee = committeeService.create(committee);

        Meeting meeting = meetingService.createTemp(user, company.getId());
        meeting.getCommittees().add(committee);
        meetingService.update(meeting);

        Question question = new Question();
        question.setNumber(1);
        question.setQuestion("Question");
        question.setSolution("Solution");
        question.setCommittee(committee);
        question.setMeeting(meeting);
        question = questionService.create(question, user);
        return question;
    }

}
