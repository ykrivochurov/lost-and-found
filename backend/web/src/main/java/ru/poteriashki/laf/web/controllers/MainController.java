package ru.poteriashki.laf.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.BaseEntity;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.model.UserRole;
import ru.poteriashki.laf.core.service.ICommentService;
import ru.poteriashki.laf.core.service.ICompanyService;
import ru.poteriashki.laf.core.service.IMeetingService;
import ru.poteriashki.laf.core.service.INotificationService;
import ru.poteriashki.laf.web.config.jsonview.ResponseView;
import ru.poteriashki.laf.web.controllers.dtos.HomeDTO;
import ru.poteriashki.laf.web.security.SecurityHelper;

import java.util.Date;

/**
 * User: y.krivochurov
 * Date: 15.05.13
 * Time: 13:12
 */
@Controller
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController {

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private INotificationService notificationService;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public
    @ResponseBody
    @ResponseView(BaseEntity.ListView.class)
    HomeDTO secretaryHome() throws JsonProcessingException {
        HomeDTO homeDTO = new HomeDTO();

        User user = SecurityHelper.getCurrentUser();
        if (CollectionUtils.isNotEmpty(user.getRoles()) && user.getRoles().contains(UserRole.ROLE_SECRETARY)) {
            homeDTO.setCompanies(companyService.loadActiveCompanies());
            homeDTO.setNearestMeetings(meetingService.findNearest(4));
            //todo we need Order by meeting.dateAndTime here
            homeDTO.setFilteredMeetings(meetingService.findByFilter(null, null,
                    new Date(), DateUtils.addMonths(new Date(), 3)));
            homeDTO.setUnreadComments(commentService.unreadCommentsCountGroupedByQuestionAllMeetings(user, 4));
            homeDTO.setUnreadCommentsCount(commentService.countOfUnreadAllMeetings(user));
        } else {
            homeDTO.setCompanies(companyService.loadActiveCompaniesByUser(user));
            homeDTO.setNearestMeetings(meetingService.findNearestByUser(user, 4));
            homeDTO.setFilteredMeetings(meetingService.findByFilterAndUser(user, null, null,
                    new Date(), DateUtils.addMonths(new Date(), 3)));
            homeDTO.setUnreadComments(commentService.unreadCommentsCountGroupedByQuestion(user, 4));
            homeDTO.setUnreadCommentsCount(commentService.countOfUnread(user));
        }

        homeDTO.setUnreadNotifications(notificationService.lastUnreadNotifications(user, 0, 4));
        homeDTO.setUnreadNotificationsCount(notificationService.countUnreadNotifications(user));
        return homeDTO;
    }

}
