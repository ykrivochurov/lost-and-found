package ru.eastbanctech.board.core.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:19
 */
@Entity
@Table(name = "user_visit_question")
@AssociationOverrides({
        @AssociationOverride(name = "userQuestion.user", joinColumns = @JoinColumn(name = "user_id")),
        @AssociationOverride(name = "userQuestion.question", joinColumns = @JoinColumn(name = "question_id"))
})
public class UserVisitQuestion implements Serializable {

    @NotNull
    @EmbeddedId
    private UserQuestion userQuestion = new UserQuestion();

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date lastVisit;

    public UserQuestion getUserQuestion() {
        return userQuestion;
    }

    public void setUserQuestion(UserQuestion userQuestion) {
        this.userQuestion = userQuestion;
    }

    public Date getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Date lastVisit) {
        this.lastVisit = lastVisit;
    }

    public void setUser(User user) {
        this.userQuestion.setUser(user);
    }

    public void setQuestion(Question question) {
        this.userQuestion.setQuestion(question);
    }

    public User getUser() {
        return userQuestion.getUser();
    }

    public Question getQuestion() {
        return userQuestion.getQuestion();
    }

    @Embeddable
    public static class UserQuestion implements Serializable {
        @NotNull
        @ManyToOne
        private User user;

        @NotNull
        @ManyToOne(cascade = CascadeType.MERGE)
        private Question question;

        public UserQuestion() {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserQuestion that = (UserQuestion) o;

            if (question != null ? !question.equals(that.question) : that.question != null) return false;
            if (user != null ? !user.equals(that.user) : that.user != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = user != null ? user.hashCode() : 0;
            result = 31 * result + (question != null ? question.hashCode() : 0);
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserVisitQuestion that = (UserVisitQuestion) o;

        if (userQuestion != null ? !userQuestion.equals(that.userQuestion) : that.userQuestion != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return userQuestion != null ? userQuestion.hashCode() : 0;
    }
}