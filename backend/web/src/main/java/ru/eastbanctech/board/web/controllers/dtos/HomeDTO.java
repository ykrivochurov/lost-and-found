package ru.eastbanctech.board.web.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonView;
import ru.eastbanctech.board.core.model.BaseEntity;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 15.05.13
 * Time: 13:12
 */
public class HomeDTO {

    @JsonView(value = {BaseEntity.ListView.class})
    private Iterable<Company> companies = new ArrayList<>();

    @JsonView(value = {BaseEntity.ListView.class})
    private List<Meeting> filteredMeetings = new ArrayList<>();

    @JsonView(value = {BaseEntity.ListView.class})
    private List<Meeting> nearestMeetings = new ArrayList<>();

    @JsonView(value = {BaseEntity.ListView.class})
    private List<Object[]> unreadComments = new ArrayList<>();

    @JsonView(value = {BaseEntity.ListView.class})
    private List<Notification> unreadNotifications = new ArrayList<>();

    @JsonView(value = {BaseEntity.ListView.class})
    private Long unreadNotificationsCount;

    @JsonView(value = {BaseEntity.ListView.class})
    private Long unreadCommentsCount;

    public Iterable<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(Iterable<Company> companies) {
        this.companies = companies;
    }

    public List<Meeting> getNearestMeetings() {
        return nearestMeetings;
    }

    public void setNearestMeetings(List<Meeting> nearestMeetings) {
        this.nearestMeetings = nearestMeetings;
    }

    public List<Object[]> getUnreadComments() {
        return unreadComments;
    }

    public void setUnreadComments(List<Object[]> unreadComments) {
        this.unreadComments = unreadComments;
    }

    public List<Notification> getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(List<Notification> unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public Long getUnreadNotificationsCount() {
        return unreadNotificationsCount;
    }

    public void setUnreadNotificationsCount(Long unreadNotificationsCount) {
        this.unreadNotificationsCount = unreadNotificationsCount;
    }

    public Long getUnreadCommentsCount() {
        return unreadCommentsCount;
    }

    public void setUnreadCommentsCount(Long unreadCommentsCount) {
        this.unreadCommentsCount = unreadCommentsCount;
    }

    public List<Meeting> getFilteredMeetings() {
        return filteredMeetings;
    }

    public void setFilteredMeetings(List<Meeting> filteredMeetings) {
        this.filteredMeetings = filteredMeetings;
    }
}
