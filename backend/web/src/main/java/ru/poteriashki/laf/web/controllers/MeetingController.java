package ru.poteriashki.laf.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.dtos.MeetingFilterDTO;
import ru.poteriashki.laf.core.model.BaseEntity;
import ru.poteriashki.laf.core.model.Meeting;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.model.UserRole;
import ru.poteriashki.laf.core.service.IMeetingService;
import ru.poteriashki.laf.core.service.ServiceException;
import ru.poteriashki.laf.web.config.jsonview.ResponseView;
import ru.poteriashki.laf.web.security.SecurityHelper;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 18:13
 */
@Controller
@RequestMapping(value = "/api/meetings", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_USER.name())")
public class MeetingController {

    @Autowired
    private IMeetingService meetingService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseView(Meeting.MeetingDetailView.class)
    public Meeting getMeeting(@PathVariable("id") Long id) throws JsonProcessingException, ServiceException {
        return meetingService.loadOne(id);
    }

    @RequestMapping(value = "/filtered", method = RequestMethod.POST)
    @ResponseBody
    @ResponseView(BaseEntity.ListView.class)
    public List<Meeting> filteredMeetings(@RequestBody MeetingFilterDTO filter) throws JsonProcessingException {
        User user = SecurityHelper.getCurrentUser();
        List<Meeting> meetings;
        if (user.getRoles().contains(UserRole.ROLE_SECRETARY)) {
            meetings = meetingService.findByFilter(filter.getStatuses(), filter.getCompanies(),
                    filter.getFromDate(), filter.getToDate());
        } else {
            meetings = meetingService.findByFilterAndUser(user, filter.getStatuses(), filter.getCompanies(),
                    filter.getFromDate(), filter.getToDate());
        }
        return meetings;
    }

    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @ResponseView(Meeting.MeetingDetailView.class)
    public Meeting createTemp(@Validated({Meeting.Temp.class}) @RequestBody Meeting meeting)
            throws JsonProcessingException, ServiceException {
        User user = SecurityHelper.getCurrentUser();
        return meetingService.createTemp(user, meeting.getCompany().getId());
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole(T(ru.poteriashki.laf.core.model.UserRole).ROLE_SECRETARY.name())")
    @ResponseView(Meeting.MeetingDetailView.class)
    @ResponseBody
    public Meeting saveDraft(@Validated({Meeting.Final.class}) @RequestBody Meeting meeting)
            throws JsonProcessingException, ServiceException {
        return meetingService.update(meeting);
    }


    @RequestMapping(value = "/agenda/meeting/{id}", method = RequestMethod.GET)
    public void loadAgenda(@PathVariable("id") Long id,
                             HttpServletResponse response) throws ServiceException {
        meetingService.loadAgenda(SecurityHelper.getCurrentUser(), id, response);
    }
}
