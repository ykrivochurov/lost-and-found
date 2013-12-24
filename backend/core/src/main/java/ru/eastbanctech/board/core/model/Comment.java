package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:09
 */
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    public static final String DATE_FIELD = "creationDate";

    @ManyToOne
    @JsonView(value = {DetailView.class, Question.QuestionView.class})
    private User author;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JsonView(value = {DetailView.class})
    private Question question;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @JsonView(value = {DetailView.class, Question.QuestionView.class})
    private Date creationDate;

    @Column
    @JsonView(value = {DetailView.class, Question.QuestionView.class})
    private String text;

    @Transient
    @JsonView(value = {DetailView.class, Question.QuestionView.class})
    private boolean notVisited;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isNotVisited() {
        return notVisited;
    }

    public void setNotVisited(boolean notVisited) {
        this.notVisited = notVisited;
    }
}