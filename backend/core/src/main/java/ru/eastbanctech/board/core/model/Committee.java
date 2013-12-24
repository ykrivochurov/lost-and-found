package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import ru.eastbanctech.board.core.dtos.BaseView;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 11:39
 */
@Entity
@Table(name = "committee")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Committee extends BaseEntity {

    public interface CommitteeView extends BaseView {}

    @NotNull
    @Column
    @JsonView(value = {DetailView.class, Meeting.MeetingDetailView.class,
            CommitteeView.class, Question.QuestionView.class})
    private String name;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(value = {CommitteeView.class, DetailView.class})
    private CommitteeStatus status;

    @ManyToOne
    @JsonView(value = {DetailView.class})
    private Company company;

    @ManyToMany
    @JsonView(value = {DetailView.class})
    private Set<User> users = new HashSet<>();

    @Transient
    @JsonView(value = {CommitteeView.class})
    private boolean canArchive;

    @Transient
    @JsonView(value = {CommitteeView.class})
    private boolean canDelete;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public boolean isCanArchive() {
        return canArchive;
    }

    public void setCanArchive(boolean canArchive) {
        this.canArchive = canArchive;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public CommitteeStatus getStatus() {

        return status;
    }

    public void setStatus(CommitteeStatus status) {

        this.status = status;
    }
}
