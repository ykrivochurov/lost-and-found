package ru.eastbanctech.board.core.init;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.eastbanctech.board.core.dao.*;
import ru.eastbanctech.board.core.model.*;
import ru.eastbanctech.board.core.service.INotificationService;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:51
 */
@Component
public class InitData {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private UserPositionInCompanyRepository userPositionInCompanyRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private VoteRepository voteRepository;

    @PostConstruct
    public void init() {
        // create user
        User yuriBulkin = new User();
        yuriBulkin.setFirstName("John");
        yuriBulkin.setMiddleName("A");
        yuriBulkin.setLastName("Doe");
        yuriBulkin.setLogin("yuri.bulkin");
        yuriBulkin.setPassword("test");
        yuriBulkin.setPhone("+7 (777) 11-22-3333");
        yuriBulkin.setEmail("yuriBulkin@email.com");
        yuriBulkin.setRoles(Sets.newHashSet(UserRole.ROLE_USER));

        yuriBulkin = userRepository.save(yuriBulkin);

        User yuriKrivochurov = new User();
        yuriKrivochurov.setFirstName("Yuri");
        yuriKrivochurov.setMiddleName("Krivochurov");
        yuriKrivochurov.setLastName("Doe");
        yuriKrivochurov.setLogin("yuri.krivochurov");
        yuriKrivochurov.setPassword("test");
        yuriKrivochurov.setPhone("+7 (777) 11-22-3333");
        yuriKrivochurov.setEmail("yuriKrivochurov@email.com");
        yuriKrivochurov.setRoles(Sets.newHashSet(UserRole.ROLE_USER));

        yuriKrivochurov = userRepository.save(yuriKrivochurov);

        User ulyaKhmeleva = new User();
        ulyaKhmeleva.setFirstName("John");
        ulyaKhmeleva.setMiddleName("A");
        ulyaKhmeleva.setLastName("Doe");
        ulyaKhmeleva.setLogin("ulya.khmeleva");
        ulyaKhmeleva.setPassword("test");
        ulyaKhmeleva.setPhone("+7 (777) 11-22-3333");
        ulyaKhmeleva.setEmail("ulyaKhmeleva@email.com");
        ulyaKhmeleva.setRoles(Sets.newHashSet(UserRole.ROLE_USER));

        ulyaKhmeleva = userRepository.save(ulyaKhmeleva);

        User alexZhukov = new User();
        alexZhukov.setFirstName("Alex");
        alexZhukov.setMiddleName("Zhukov");
        alexZhukov.setLastName("Doe");
        alexZhukov.setLogin("alex.zhukov");
        alexZhukov.setPassword("test");
        alexZhukov.setPhone("+7 (777) 11-22-3333");
        alexZhukov.setEmail("alexZhukov@email.com");
        alexZhukov.setRoles(Sets.newHashSet(UserRole.ROLE_USER));

        alexZhukov = userRepository.save(alexZhukov);

        User user = new User();
        user.setFirstName("John");
        user.setMiddleName("A");
        user.setLastName("Doe");
        user.setLogin("jad");
        user.setPassword("test");
        user.setPhone("+7 (777) 11-22-3333");
        user.setEmail("test@email.com");
        user.setRoles(Sets.newHashSet(UserRole.ROLE_USER));

        user = userRepository.save(user);

        // create secretary
        User secretary = new User();
        secretary.setFirstName("Secretary");
        secretary.setMiddleName("A");
        secretary.setLastName("Doe");
        secretary.setLogin("secretary");
        secretary.setPassword("test");
        secretary.setPhone("+7 (777) 11-22-3456");
        secretary.setEmail("secretary@email.com");
        secretary.setRoles(Sets.newHashSet(UserRole.ROLE_SECRETARY, UserRole.ROLE_USER));
        secretary = userRepository.save(secretary);

        for (int i = 0; i < 20; i ++) {
            User tUser = new User();
            tUser.setFirstName("Jack" + i);
            tUser.setMiddleName("Johnson");
            tUser.setLogin("JJ" + i);
            tUser.setPassword("test");
            tUser.setRoles(Sets.newHashSet(UserRole.ROLE_USER));

            userRepository.save(tUser);
        }

        // create company
        Company company = new Company();
        company.setStatus(CompanyStatus.ACTIVE);
        company.setName("Init Company");

        company = companyRepository.save(company);

        UserPositionInCompany userPositionInCompany = new UserPositionInCompany();
        userPositionInCompany.setPosition("Director");
        userPositionInCompany.setCompany(company);
        userPositionInCompany.setUser(user);
        userPositionInCompany = userPositionInCompanyRepository.save(userPositionInCompany);

        userPositionInCompany = new UserPositionInCompany();
        userPositionInCompany.setPosition("Developer");
        userPositionInCompany.setCompany(company);
        userPositionInCompany.setUser(alexZhukov);
        userPositionInCompany = userPositionInCompanyRepository.save(userPositionInCompany);

/*        company.getPositionInCompanies().add(userPositionInCompany);
        companyRepository.save(company);

        user.getPositions().add(userPositionInCompany);
        userRepository.save(user);*/

        company = companyRepository.findOne(company.getId());
        Committee committee = null;

        for (int i = 0; i < 5; i++) {
            // create committee
            committee = new Committee();
            committee.setName("Test committee name " + i);
            committee.setCompany(company);
            committee.setStatus(CommitteeStatus.ACTIVE);
/*            committee.getUsers().add(user);
            committee.getUsers().add(yuriBulkin);
            committee.getUsers().add(yuriKrivochurov);
            committee.getUsers().add(ulyaKhmeleva);*/

            committee = committeeRepository.save(committee);
        }

        committee = new Committee();
        committee.setName("One more committee");
        committee.setCompany(company);
        committee.setStatus(CommitteeStatus.ACTIVE);
        committee.getUsers().add(alexZhukov);

        committee = committeeRepository.save(committee);


        // create meeting
        Meeting meeting = new Meeting();
        meeting.setAuthor(user);
        meeting.setAddress("Test address");
        meeting.setCommittees(Arrays.asList(committee));
        meeting.setCompany(company);
        meeting.setCreationDate(new Date());
        meeting.setDateAndTime(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));
        meeting.setPersonalPresence(true);
        meeting.setName("Our first meeting");
        meeting.setStatus(MeetingStatus.VOTING_OPENED);

        meeting = meetingRepository.save(meeting);

        for (int i = 0; i < 3; i++) {
            // create question
            Question question = questionWithComments(user, committee, meeting, i + 1, i + 1);
            meeting.getQuestions().add(question);
            meetingRepository.save(meeting);
        }

        createNotifications(user);
    }

    private void createNotifications(User user) {
        for (int i = 0; i < 5; i++) {
            Notification notification = new Notification();
            notification.setText("Test notification " + System.currentTimeMillis());
            notificationService.create(notification, user);
        }
    }

    private Question questionWithComments(User user, Committee committee, Meeting meeting,
                                          int number, int commentCount) {
        Question question1 = new Question();
        question1.setNumber(number);
        question1.setAuthor(user);
        question1.setCommittee(committee);
        question1.setCreationDate(new Date());
        question1.setPerformerUser("Bob");
        question1.setQuestion("Test question for voting");
        question1.setSolution("Solution");
        question1.setMeeting(meeting);
        question1.setLastModify(new Date());
        question1.setStatus(QuestionStatus.OnReview);

        question1 = questionRepository.save(question1);

        for (int i = 0; i < 10; i++) {
            Vote vote = new Vote();
            vote.setQuestion(question1);
            vote.setUser(user);
            vote.setVoteState((i % 3 == 0) ? VoteState.yes : ((i % 3 == 1) ? VoteState.no : VoteState.abstain));
            vote.setCreationDate(new Date());
            voteRepository.save(vote);
        }

        for (int i = 0; i < commentCount; i++) {
            // create comment
            Comment comment = new Comment();
            comment.setAuthor(user);
            comment.setCreationDate(new Date());
            comment.setText("Test comment text");
            comment.setQuestion(question1);
            commentRepository.save(comment);
        }
        return question1;
    }

}
