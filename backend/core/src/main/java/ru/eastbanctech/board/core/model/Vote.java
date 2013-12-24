package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.format.annotation.DateTimeFormat;
import ru.eastbanctech.board.core.dtos.BaseView;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:12
 */
@Entity
@Table(name = "vote")
public class Vote extends BaseEntity {

    public interface VoteView extends BaseView {}

    @Column
    @JsonView(value = {Meeting.MeetingDetailView.class, VoteView.class})
    private VoteState voteState;

    @ManyToOne
    @JsonView(value = {VoteView.class})
    private User user;

    @ManyToOne
    @JsonView(value = {DetailView.class})
    private Question question;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(value = {DetailView.class})
    @NotNull
    private Date creationDate;

    public VoteState getVoteState() {
        return voteState;
    }

    public void setVoteState(VoteState voteState) {
        this.voteState = voteState;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}