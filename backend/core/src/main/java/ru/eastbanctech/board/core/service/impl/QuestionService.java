package ru.eastbanctech.board.core.service.impl;

import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ru.eastbanctech.board.core.dao.CommitteeRepository;
import ru.eastbanctech.board.core.dao.QuestionRepository;
import ru.eastbanctech.board.core.dao.ResourceRepository;
import ru.eastbanctech.board.core.dao.UserVisitQuestionRepository;
import ru.eastbanctech.board.core.model.Comment;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.QuestionStatus;
import ru.eastbanctech.board.core.model.Resource;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserRole;
import ru.eastbanctech.board.core.model.UserVisitQuestion;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.IMeetingService;
import ru.eastbanctech.board.core.service.IQuestionService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.resources.services.IResourceService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 16:21
 */
@Service
@Transactional
public class QuestionService implements IQuestionService {

    public static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .append(DateTimeFormat.forPattern("ddMMyyyy").getPrinter())
            .toFormatter();

    public static final String ZIP_FILE_NAME = "attachment; filename=\"meeting_%s_question_%s.zip\"";

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserVisitQuestionRepository userVisitQuestionRepository;

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Question create(Question question, User user) throws ServiceException {
        Assert.notNull(user, "'user' parameter shouldn't be empty");
        Assert.notNull(question, "'question' parameter shouldn't be empty");
        Assert.notNull(question.getMeeting(), "'question.meeting' parameter shouldn't be empty");
        Assert.notNull(question.getMeeting().getId(), "'question.meeting.id' parameter shouldn't be empty");
        Assert.notNull(question.getCommittee(), "'question.committee' parameter shouldn't be empty");
        Assert.notNull(question.getCommittee().getId(), "'question.committee.id' parameter shouldn't be empty");

        Meeting meeting = meetingService.loadOne(question.getMeeting().getId());
        Committee committee = committeeService.loadOne(question.getCommittee().getId());

        question.setCreationDate(new Date());
        question.setLastModify(new Date());
        question.setMeeting(meeting);
        question.setCommittee(committee);
        question.setAuthor(user);
        question.setStatus(QuestionStatus.Include);

        return questionRepository.save(question);
    }

    @Override
    public Question update(Question question, User user) throws ServiceException {
        Assert.notNull(question, "'question' parameter shouldn't be empty");
        Assert.notNull(user, "'user' parameter shouldn't be empty");

        Question existingQuestion = getQuestion(question.getId());

        if (!existingQuestion.getAuthor().equals(user) && !user.getRoles().contains(UserRole.ROLE_SECRETARY)) {
            throw new ServiceException(ErrorType.ACCESS_DENIED, "Only author or secretary may update question id="
                    + question.getId());
        }

        existingQuestion.setSolution(question.getSolution());
        existingQuestion.setQuestion(question.getQuestion());
        existingQuestion.setStatus(question.getStatus());
        existingQuestion.setPerformerUser(question.getPerformerUser());
        existingQuestion.setLastModify(new Date());

        if (question.getCommittee() != null && question.getCommittee().getId() != null) {
            Committee committee = committeeRepository.findOne(question.getCommittee().getId());
            if (committee != null) {
                existingQuestion.setCommittee(committee);
            }
        }

        // update questions numbers
        if (question.getNumber() != null && !question.getNumber().equals(existingQuestion.getNumber())) {
            List<Question> questions =
                    questionRepository.findByMeetingIdOrderByNumberAsc(existingQuestion.getMeeting().getId());

            List<Question> toSort = new ArrayList<>();
            toSort.addAll(questions);
            existingQuestion.setNumber(question.getNumber());
            toSort.remove(existingQuestion);
            toSort.add(existingQuestion.getNumber() - 1, existingQuestion);
            for (int i = 0; i < toSort.size(); i++) {
                Question questionWithNewNumber = toSort.get(i);
                questionWithNewNumber.setNumber(i + 1);
                questionRepository.updateNumber(questionWithNewNumber.getNumber(), questionWithNewNumber.getId());
            }
        }

        return questionRepository.save(existingQuestion);
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "'id' parameter shouldn't be empty");
        questionRepository.delete(id);
    }

    @Override
    public void userVisitQuestion(User user, Question question) {
        Assert.notNull(user, "'user' parameter shouldn't be empty");
        Assert.notNull(question, "'question' parameter shouldn't be empty");

        UserVisitQuestion userVisitQuestion = userVisitQuestionRepository.
                findByUserQuestionUserIdAndUserQuestionQuestionId(user.getId(), question.getId());
        if (userVisitQuestion == null) {
            userVisitQuestion = new UserVisitQuestion();
            userVisitQuestion.setQuestion(question);
            userVisitQuestion.setUser(user);
        }
        userVisitQuestion.setLastVisit(new Date());

        userVisitQuestionRepository.save(userVisitQuestion);
    }

    @Override
    public void loadDocument(User user, Long id, HttpServletResponse response) throws ServiceException {
        Assert.notNull(id, "'id' parameter shouldn't be empty");
        Assert.notNull(user, "'user' parameter shouldn't be empty");

        Resource resource = resourceRepository.findOne(id);
        if (resource == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Resource not found id=" + id);
        }

        Question existingQuestion = getQuestion(resource.getQuestion().getId());

        checkAccessToQuestion(user, existingQuestion);

        try {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + resource.getName() + "\"");
            response.addHeader("Content-Transfer-Encoding", "binary");
            resourceService.writeToResponse(resource.getDocumentId(), response);
        } catch (Exception e) {
            throw new ServiceException(ErrorType.DATABASE_ERROR, e);
        }
    }

    @Override
    public void uploadDocument(User user, MultipartFile document, Long questionId) throws ServiceException {
        Assert.notNull(user, "'user' parameter shouldn't be empty");
        Assert.notNull(document, "'document' parameter shouldn't be empty");
        Assert.isTrue(document.getSize() > 0, "'document' size should be more then 0");
        Assert.notNull(questionId, "'questionId' parameter shouldn't be empty");

        Question existingQuestion = getQuestion(questionId);

        checkAccessToQuestion(user, existingQuestion);

        try {
            String documentId = resourceService.saveMultipart(document);
            Resource resource = new Resource();
            resource.setAuthor(user);
            resource.setDocumentId(documentId);
            resource.setQuestion(existingQuestion);
            resource.setCreationDate(new Date());
            resource.setName(document.getOriginalFilename());
            resource = resourceRepository.save(resource);

            existingQuestion.getResources().add(resource);
            questionRepository.save(existingQuestion);
        } catch (Exception e) {
            throw new ServiceException(ErrorType.DATABASE_ERROR, e);
        }
    }

    @Override
    public Question getByMeetingIdAndNumber(Long meetingId, Integer questNum) {
        Assert.notNull(meetingId, "'meetingId' parameter shouldn't be empty");
        Assert.notNull(questNum, "'questNum' parameter shouldn't be empty");

        return questionRepository.findByMeetingIdAndNumber(meetingId, questNum);
    }

    @Override
    public void deleteDocument(Long id) throws ServiceException {
        Assert.notNull(id, "'id' parameter shouldn't be empty");

        try {
            Resource resource = resourceRepository.findOne(id);
            if (resource == null) {
                return;
            }
            resourceService.delete(resource.getDocumentId());
            resourceRepository.delete(id);
        } catch (ru.eastbanctech.resources.services.ServiceException e) {
            throw new ServiceException(ErrorType.DATABASE_ERROR, e);
        }
    }

    @Override
    public Question getByIdAndUser(Long questionId, User user) throws ServiceException {
        Assert.notNull(questionId, "'questionId' parameter shouldn't be empty");
        Assert.notNull(user, "'user' parameter shouldn't be empty");
        Question question = getQuestion(questionId);

        checkAccessToQuestion(user, question);

        Date userLastVisitDate = null;

        UserVisitQuestion userVisitQuestion = userVisitQuestionRepository.
                findByUserQuestionUserIdAndUserQuestionQuestionId(user.getId(), question.getId());
        if (userVisitQuestion == null) {
            userVisitQuestion = new UserVisitQuestion();
            userVisitQuestion.setQuestion(question);
            userVisitQuestion.setUser(user);
        } else {
            userLastVisitDate = userVisitQuestion.getLastVisit();
        }
        userVisitQuestion.setLastVisit(new Date());
        userVisitQuestionRepository.save(userVisitQuestion);

        for (Comment comment : question.getComments()) {
            comment.setNotVisited(userLastVisitDate == null ||
                    (comment.getCreationDate().after(userLastVisitDate) && !comment.getAuthor().equals(user)));
        }
        return question;
    }

    @Override
    public void loadZippedDocuments(User user, Long questionId, HttpServletResponse response) throws ServiceException {
        Assert.notNull(user, "'user' parameter shouldn't be empty");
        Assert.notNull(questionId, "'questionId' parameter shouldn't be empty");

        Question existingQuestion = getQuestion(questionId);

        checkAccessToQuestion(user, existingQuestion);

        try {
            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputBuffer);
            zipOutputStream.setLevel(ZipOutputStream.STORED);
            for (Resource resource : existingQuestion.getResources()) {
                GridFSDBFile gridFSDBFile = resourceService.load(resource.getDocumentId());
                addOneFileToZipArchive(zipOutputStream, resource.getName(), gridFSDBFile.getInputStream());
            }
            zipOutputStream.close();
            response.setContentType("application/zip");
            response.addHeader("Content-Disposition", String.format(ZIP_FILE_NAME,
                    FORMATTER.print(existingQuestion.getMeeting().getId()),
                    existingQuestion.getNumber()));
            response.addHeader("Content-Transfer-Encoding", "binary");
            response.getOutputStream().write(outputBuffer.toByteArray());
            response.getOutputStream().flush();
            outputBuffer.close();
        } catch (Exception e) {
            throw new ServiceException(ErrorType.DATABASE_ERROR, e);
        }
    }

    private void addOneFileToZipArchive(final ZipOutputStream zipStream,
                                        String fileName,
                                        InputStream inputStream) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipStream.putNextEntry(zipEntry);
        IOUtils.copy(inputStream, zipStream);
        zipStream.closeEntry();
    }

    private void checkAccessToQuestion(User user, Question existingQuestion) throws ServiceException {
        if (user.getRoles().contains(UserRole.ROLE_SECRETARY)) {
            return;
        }
        boolean allowUpload = false;
        for (Committee committee : existingQuestion.getMeeting().getCommittees()) {
            if (committee.getUsers().contains(user)) {
                allowUpload = true;
            }
        }

        if (!allowUpload) {
            throw new ServiceException(ErrorType.ACCESS_DENIED,
                    "Only secretary or user from meeting have access to question");
        }
    }

    @Override
    public Question loadOne(Long questionId) throws ServiceException {
        return getQuestion(questionId);
    }

    private Question getQuestion(Long questionId) throws ServiceException {
        Assert.notNull(questionId, "'questionId' parameter shouldn't be empty");
        Question question = questionRepository.findOne(questionId);
        if (question == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Question not found id=" + questionId);
        }
        return question;
    }
}