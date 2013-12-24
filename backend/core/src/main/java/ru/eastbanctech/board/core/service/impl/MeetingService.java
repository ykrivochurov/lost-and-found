package ru.eastbanctech.board.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import ru.eastbanctech.board.core.dao.MeetingRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.MeetingStatus;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserRole;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IMeetingService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.reports.services.IReportService;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 18:11
 */
@Service
@Transactional
public class MeetingService implements IMeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private IReportService reportService;

    @Override
    public Meeting loadOne(Long id) throws ServiceException {
        return getExistingMeeting(id);
    }

    @Override
    public Meeting createTemp(User user, Long companyId) throws ServiceException {
        Assert.notNull(user);
        Assert.notNull(companyId);

        Company company = companyService.loadOne(companyId);

        Meeting meeting = new Meeting();
        meeting.setName("");
        meeting.setCompany(company);
        meeting.setAuthor(user);
        meeting.setStatus(MeetingStatus.TEMP);
        meeting.setCreationDate(new Date());

        return meetingRepository.save(meeting);
    }

    @Override
    public Meeting update(Meeting meeting) throws ServiceException {
        Assert.notNull(meeting, "'meeting' parameter shouldn't be empty");
        Assert.notNull(meeting.getId(), "'meeting.id' parameter shouldn't be empty");
        Assert.notNull(meeting.getStatus(), "'meeting.status' parameter shouldn't be empty");

        Meeting existingMeeting = getExistingMeeting(meeting.getId());
        updateInternal(meeting, existingMeeting);
        return meetingRepository.save(existingMeeting);
    }

    @Override
    public List<Meeting> findNearest(int count) {
        Page<Meeting> meetingsWithCounts = meetingRepository.findNearest(new Date(),
                new PageRequest(0, count, new Sort(new Sort.Order(Sort.Direction.ASC, "dateAndTime"))));
        return meetingsWithCounts.getContent();
    }

    @Override
    public List<Meeting> findByFilter(List<MeetingStatus> statuses, List<Company> companies,
                                      Date fromDate, Date toDate) {
        return meetingRepository.findByCompanyIn(null, companies, fromDate, toDate, statuses);
    }

    @Override
    public List<Meeting> findNearestByUser(User user, int count) {
        Page<Meeting> meetingsWithCounts = meetingRepository.findNearestByUser(user, new Date(),
                new PageRequest(0, count, new Sort(new Sort.Order(Sort.Direction.ASC, "dateAndTime"))));
        return meetingsWithCounts.getContent();
    }

    @Override
    public List<Meeting> findByFilterAndUser(User user, List<MeetingStatus> statuses, List<Company> companies,
                                             Date fromDate, Date toDate) {
        return meetingRepository.findByCompanyIn(user, companies, fromDate, toDate, statuses);
    }

    private Meeting getExistingMeeting(Long meetingId) throws ServiceException {
        Assert.notNull(meetingId, "'meetingId' parameter shouldn't be empty");

        Meeting existingMeeting = meetingRepository.findOne(meetingId);
        if (existingMeeting == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Meeting doesn't exist id=" + meetingId);
        }
        Collections.sort(existingMeeting.getQuestions(), new Comparator<Question>() {
            @Override
            public int compare(Question o1, Question o2) {
                int result = o1.getNumber() - o2.getNumber();
                return (result != 0) ? result/Math.abs(result) : 0;
            }
        });
        return existingMeeting;
    }

    private void updateInternal(Meeting meeting, Meeting existingMeeting) throws ServiceException {
        existingMeeting.setName(meeting.getName());
        existingMeeting.setDateAndTime(meeting.getDateAndTime());
        existingMeeting.setAddress(meeting.getAddress());
        existingMeeting.setPersonalPresence(meeting.getPersonalPresence());
        existingMeeting.setStatus(meeting.getStatus());

        if (!CollectionUtils.isEmpty(meeting.getCommittees())) {
            existingMeeting.setCommittees(committeeService.refreshCommittees(meeting.getCommittees()));
        }
    }

    @Override
    public void loadAgenda(User user, Long id, HttpServletResponse response) throws ServiceException {
        Assert.notNull(id, "'id' parameter shouldn't be empty");
        Assert.notNull(user, "'user' parameter shouldn't be empty");

        Meeting existingMeeting = getExistingMeeting(id);

        if (!user.getRoles().contains(UserRole.ROLE_SECRETARY)) {
            boolean allowUpload = false;
            for (Committee committee : existingMeeting.getCommittees()) {
                if (committee.getUsers().contains(user)) {
                    allowUpload = true;
                }
            }
            if (!allowUpload) {
                throw new ServiceException(ErrorType.ACCESS_DENIED, "Only user from meeting may upload agenda");
            }
        }

        try {
            Map<String, Object> reportParameters = new HashMap<>();
            String committeesString = "";
            for (Committee committee : existingMeeting.getCommittees()) {
                committeesString += committee.getName() + '\n';
            }
            reportParameters.put("MeetingName", existingMeeting.getName());
            reportParameters.put("MeetingDate", existingMeeting.getDateAndTime());
            reportParameters.put("MeetingPlace", existingMeeting.getAddress());
            reportParameters.put("MeetingForm", existingMeeting.getPersonalPresence() ? "Очно" : "Заочно");
            reportParameters.put("MeetingCompany", existingMeeting.getCompany().getName());
            reportParameters.put("MeetingCommittees", committeesString);

            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + "agenda.pdf" + "\"");
            response.addHeader("Content-Transfer-Encoding", "binary");
            reportService.writeAgendaToResponse("agendaTemplate.jrxml",
                    reportParameters, existingMeeting.getQuestions(), response);
        } catch (Exception e) {
            throw new ServiceException(ErrorType.DATABASE_ERROR, e);
        }
    }
}
