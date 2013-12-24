package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.format.annotation.DateTimeFormat;
import ru.eastbanctech.board.core.dtos.BaseView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 14:59
 */
@Entity
@Table(name = "question")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question extends BaseEntity {

    public interface QuestionView extends BaseView {
    }

    @ManyToOne
    @NotNull
    @JsonView(value = {ListView.class, QuestionView.class, Vote.VoteView.class})
    private Meeting meeting;

    @Column(name = "number")
    @NotNull
    @JsonView(value = {ListView.class, Meeting.MeetingDetailView.class, QuestionView.class, Vote.VoteView.class})
    private Integer number;

    @Column(length = 2000)
    @Size(max = 2000, min = 3)
    @NotNull
    @JsonView(value = {Meeting.MeetingDetailView.class, QuestionView.class, Vote.VoteView.class})
    private String question;

    @Column(length = 2000)
    @Size(max = 2000, min = 3)
    @NotNull
    @JsonView(value = {Meeting.MeetingDetailView.class, QuestionView.class, Vote.VoteView.class})
    private String solution;

    @ManyToOne
    @NotNull
    @JsonView(value = {Meeting.MeetingDetailView.class, QuestionView.class})
    private Committee committee;

    @Column
    @JsonView(value = {Meeting.MeetingDetailView.class, QuestionView.class})
    private String performerUser;

    @OneToMany(mappedBy = "question")
    @JsonView(value = {Meeting.MeetingDetailView.class, QuestionView.class})
    private List<Resource> resources = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    @JsonView(value = {QuestionView.class})
    private List<Comment> comments = new ArrayList<>();

    @Column
    @JsonView(value = {Meeting.MeetingDetailView.class, QuestionView.class, Vote.VoteView.class})
    private QuestionStatus status;

    @OneToMany(mappedBy = "question")
    @JsonView(value = {Meeting.MeetingDetailView.class, Vote.VoteView.class})
    private List<Vote> votes;

    @OneToMany(mappedBy = "userQuestion.question")
    @JsonView(value = {DetailView.class})
    private List<UserVisitQuestion> userVisitQuestions = new ArrayList<>();

    @Column
    @JsonView(value = {DetailView.class})
    private VotingResult votingResult;

    @ManyToOne
    @JsonView(value = {Meeting.MeetingDetailView.class, QuestionView.class})
    private User author;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(value = {DetailView.class})
    private Date creationDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(value = {ListView.class, QuestionView.class})
    private Date lastModify;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public Committee getCommittee() {
        return committee;
    }

    public void setCommittee(Committee committee) {
        this.committee = committee;
    }

    public String getPerformerUser() {
        return performerUser;
    }

    public void setPerformerUser(String performerUser) {
        this.performerUser = performerUser;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public VotingResult getVotingResult() {
        return votingResult;
    }

    public void setVotingResult(VotingResult votingResult) {
        this.votingResult = votingResult;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<UserVisitQuestion> getUserVisitQuestions() {
        return userVisitQuestions;
    }

    public void setUserVisitQuestions(List<UserVisitQuestion> userVisitQuestions) {
        this.userVisitQuestions = userVisitQuestions;
    }

    public Date getLastModify() {
        return lastModify;
    }

    public void setLastModify(Date lastModify) {
        this.lastModify = lastModify;
    }
}
