package ru.eastbanctech.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.dao.MeetingRepository;
import ru.eastbanctech.board.core.dao.QuestionRepository;
import ru.eastbanctech.board.core.dao.VoteRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.MeetingStatus;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.Vote;
import ru.eastbanctech.board.core.model.VoteState;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.core.service.IVotingService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.util.TestHelper;

import java.util.Date;

/**
 * User: a.zhukov
 * Date: 17.06.13
 * Time: 13:53
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class,
        TestPersistenceJPAConfig.class})
public class VotingServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private IVotingService votingService;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private Vote initAction() throws ServiceException {
        User user = userService.createOrUpdate(TestHelper.createUser());
        Company company = TestHelper.createCompany();
        company = companyService.create(company);

        Committee committee = TestHelper.createCommittee(company);
        committee.getUsers().add(user);
        committee = committeeService.create(committee);

        Meeting meeting = new Meeting();
        meeting.setName("TestName");
        meeting.setStatus(MeetingStatus.VOTING_OPENED);
        meeting.setCreationDate(new Date());
        Question question = new Question();
        question.setMeeting(meeting);
        question.setNumber(1);
        question.setQuestion("TestQuestion");
        question.setSolution("TestSolution");
        question.setAuthor(user);
        question.setCreationDate(new Date());
        question.setLastModify(new Date());
        question.setCommittee(committee);
        meeting.getQuestions().add(question);
        meetingRepository.save(meeting);
        question = questionRepository.save(question);

        Vote vote = new Vote();
        vote.setVoteState(VoteState.yes);
        vote.setQuestion(question);
        vote.setUser(user);
        vote.setCreationDate(new Date());
        return vote;
    }

    @Test
    public void testCreate() throws Exception {
        Vote vote = initAction();
        Question question = vote.getQuestion();
        User user = question.getAuthor();
        vote = votingService.create(vote);


        vote = voteRepository.findOne(vote.getId());
        Assert.assertEquals(vote.getUser(), user);
        Assert.assertEquals(vote.getQuestion(), question);
    }

    @Test
    public void testDelete() throws Exception {
        Vote vote = initAction();
        vote = votingService.create(vote);
        votingService.delete(vote.getId());

        vote = voteRepository.findOne(vote.getId());
        Assert.assertEquals(vote, null);
    }

    @Test
    public void testUpdate() throws Exception {
        Vote vote = initAction();
        vote = votingService.create(vote);
        vote.setVoteState(VoteState.no);
        votingService.update(vote);

        vote = voteRepository.findOne(vote.getId());
        Assert.assertEquals(vote.getVoteState(), VoteState.no);
    }

    @Test
    public void testFindById() throws Exception {
        Vote vote = initAction();
        vote = votingService.create(vote);

        Vote dbVote = votingService.findById(vote.getId(), vote.getUser());
        Assert.assertEquals(vote, dbVote);
    }

}
