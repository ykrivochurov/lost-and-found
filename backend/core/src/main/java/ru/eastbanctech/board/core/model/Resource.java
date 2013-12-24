package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
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
 * Time: 15:07
 */
@Entity
@Table(name = "resource")
public class Resource extends BaseEntity {

    @ManyToOne
    @NotNull
    @JsonView(value = {BaseEntity.DetailView.class})
    private User author;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JsonView(value = {BaseEntity.DetailView.class})
    private Question question;

    @Column
    @JsonView(value = {Meeting.MeetingDetailView.class, Question.QuestionView.class})
    private String documentId;

    @Column
    @JsonView(value = {Meeting.MeetingDetailView.class, Question.QuestionView.class})
    private String name;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @JsonView(value = {Meeting.MeetingDetailView.class, Question.QuestionView.class})
    private Date creationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

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
}
