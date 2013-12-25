package ru.poteriashki.laf.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import ru.poteriashki.laf.core.repositories.QuestionRepository;
import ru.poteriashki.laf.core.model.Meeting;
import ru.poteriashki.laf.core.model.Question;
import ru.poteriashki.laf.core.model.Vote;
import ru.poteriashki.laf.core.service.IQuestionService;
import ru.poteriashki.laf.core.service.ServiceException;
import ru.poteriashki.laf.web.config.jsonview.ResponseView;
import ru.poteriashki.laf.web.security.SecurityHelper;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * User: y.krivochurov
 * Date: 06.06.13
 * Time: 11:06
 */
@Controller
@RequestMapping(value = "/api/questions", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_USER.name())")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private IQuestionService questionService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @ResponseView(Meeting.MeetingDetailView.class)
    public Question create(@Valid @RequestBody Question question)
            throws ServiceException, JsonProcessingException {
        return questionService.create(question, SecurityHelper.getCurrentUser());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseView(Question.QuestionView.class)
    public Question getById(@PathVariable("id") Long id) throws JsonProcessingException, ServiceException {
        return questionService.getByIdAndUser(id, SecurityHelper.getCurrentUser());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ResponseView(Meeting.MeetingDetailView.class)
    public Question update(@RequestBody Question question) throws ServiceException, JsonProcessingException {
        return questionService.update(question, SecurityHelper.getCurrentUser());
    }

    //todo we need cascade for committee here
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) throws ServiceException {
        questionService.delete(id);
    }

    @RequestMapping(value = "/meeting/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @ResponseBody
    @ResponseView(Meeting.MeetingDetailView.class)
    public Iterable<Question> byMeetingId(@PathVariable("id") Long meetingId) {
        return questionRepository.findByMeetingIdOrderByNumberAsc(meetingId);
    }

    @RequestMapping(value = "/meeting/{id}/number/{number}", method = RequestMethod.GET)
    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @ResponseBody
    @ResponseView(Vote.VoteView.class)
    public Question byMeetingIdAndNumber(@PathVariable("id") Long meetingId, @PathVariable("number") Integer questNum) {
        return questionService.getByMeetingIdAndNumber(meetingId, questNum);
    }

    @RequestMapping(value = "/document", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    public void uploadDocument(MultipartHttpServletRequest request,
                               @ModelAttribute("id") Long id,
                               Model model, HttpServletResponse response) throws ServiceException {
        for (MultipartFile multipartFile : request.getFileMap().values()) {
            questionService.uploadDocument(SecurityHelper.getCurrentUser(), multipartFile, id);
        }
    }

    @RequestMapping(value = "/document/{id}", method = RequestMethod.GET)
    public void loadDocument(@PathVariable("id") Long id,
                             HttpServletResponse response) throws ServiceException {
        questionService.loadDocument(SecurityHelper.getCurrentUser(), id, response);
    }

    @RequestMapping(value = "/document/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    public void deleteDocument(@PathVariable("id") Long id, HttpServletResponse response) throws ServiceException {
        questionService.deleteDocument(id);
    }

    @RequestMapping(value = "/document/zipped/{questionId}", method = RequestMethod.GET)
    public void loadZippedDocuments(@PathVariable("questionId") Long id,
                                    HttpServletResponse response) throws ServiceException {
        questionService.loadZippedDocuments(SecurityHelper.getCurrentUser(), id, response);
    }

}
