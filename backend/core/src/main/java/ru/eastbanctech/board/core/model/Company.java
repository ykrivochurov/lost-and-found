package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonView;
import ru.eastbanctech.board.core.dtos.BaseView;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 12:47
 */
@Entity
@Table(name = "company", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Company extends BaseEntity {

    public interface CompanyView extends BaseView {}

    @NotNull
    @Column
    @JsonView(value = {CompanyView.class, ListView.class, Meeting.MeetingDetailView.class})
    private String name;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(value = {CompanyView.class})
    private CompanyStatus status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userCompany.company", orphanRemoval = true)
    @JsonView(value = {DetailView.class})
    private List<UserPositionInCompany> positionInCompanies = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", orphanRemoval = true)
    @JsonView(value = {DetailView.class})
    private List<Committee> committees = new ArrayList<>();

    @Transient
    @JsonView(value = {Company.CompanyView.class})
    private boolean canArchive;

    @Transient
    @JsonView(value = {Company.CompanyView.class})
    private boolean canDelete;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserPositionInCompany> getPositionInCompanies() {
        return positionInCompanies;
    }

    public void setPositionInCompanies(List<UserPositionInCompany> positionInCompanies) {
        this.positionInCompanies = positionInCompanies;
    }

    public List<Committee> getCommittees() {
        return committees;
    }

    public void setCommittees(List<Committee> committees) {
        this.committees = committees;
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

    public CompanyStatus getStatus() {

        return status;
    }

    public void setStatus(CompanyStatus status) {

        this.status = status;
    }
}
