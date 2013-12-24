package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * User: y.krivochurov
 * Date: 02.05.13
 * Time: 14:52
 */
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    @JsonView(value = {ListView.class})
    private String text;

//    private ? type;

    @ManyToOne
    @JsonView(value = {DetailView.class})
    private User user;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @JsonView(value = {DetailView.class})
    private Date creationDate;

    @Column
    @JsonView(value = {DetailView.class})
    private Boolean visited;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean isVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }
}
