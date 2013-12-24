package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.format.annotation.DateTimeFormat;
import ru.eastbanctech.board.core.dtos.BaseView;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 14:55
 */
@Entity
@Table(name = "meeting")
public class Meeting extends BaseEntity {

    public interface MeetingDetailView extends BaseView {}
    public interface Temp {}
    public interface Final {}

    @Column
    @JsonView(value = {ListView.class, MeetingDetailView.class, Question.QuestionView.class, Vote.VoteView.class})
    @NotNull(groups = {Final.class})
    private String name;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(groups = {Final.class})
    @JsonView(value = {ListView.class, MeetingDetailView.class, Question.QuestionView.class})
    private Date dateAndTime;

    @Column
    @NotNull(groups = {Final.class})
    @JsonView(value = {ListView.class, MeetingDetailView.class, Question.QuestionView.class})
    private String address;

    @Column
    @JsonView(value = {MeetingDetailView.class})
    private Boolean personalPresence;

    @ManyToMany
    @NotNull(groups = {Final.class})
    @Size(min = 1, groups = {Final.class})
    @JsonView(value = {MeetingDetailView.class})
    private List<Committee> committees = new ArrayList<>();

    @OneToMany(mappedBy = "meeting")
    @JsonView(value = {MeetingDetailView.class})
    private List<Question> questions = new ArrayList<>();

    @Column
    @NotNull(groups = {Final.class})
    @Enumerated(value = EnumType.STRING)
    @JsonView(value = {ListView.class, MeetingDetailView.class})
    private MeetingStatus status;

    @ManyToOne
    @JsonView(value = {MeetingDetailView.class})
    private User author;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(value = {MeetingDetailView.class})
    private Date creationDate;

    @ManyToOne
    @NotNull(groups = {Final.class, Temp.class})
    @JsonView(value = {MeetingDetailView.class})
    private Company company;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Date dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getPersonalPresence() {
        return personalPresence;
    }

    public void setPersonalPresence(Boolean personalPresence) {
        this.personalPresence = personalPresence;
    }

    public List<Committee> getCommittees() {
        return committees;
    }

    public void setCommittees(List<Committee> committees) {
        this.committees = committees;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @JsonView(value = {ListView.class, Vote.VoteView.class})
    public long getQuestionsCount() {
        if (questions.isEmpty()) {
            return 0l;
        } else {
            return questions.size();
        }
    }

    @JsonView(value = {ListView.class})
    public long getUsersCount() {
        if (committees.isEmpty()) {
            return 0l;
        } else {
            long usersCount = 0l;
            for (Committee committee : committees) {
                usersCount += committee.getUsers().size();
            }
            return usersCount;
        }
    }
}
