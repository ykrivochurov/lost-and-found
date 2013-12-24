package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.eastbanctech.board.core.dtos.BaseView;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 11:32
 */
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    public interface UserView extends BaseView {}

    @Column(unique = true)
    @NotNull
    @JsonView(value = {UserView.class, ListView.class, UserPositionInCompany.PositionView.class})
    private String login;

    @JsonIgnore
    @Column
    private String password;

    @JsonView(value = {UserView.class, ListView.class, UserPositionInCompany.PositionView.class,
            Question.QuestionView.class, Vote.VoteView.class})
    @Column
    private String firstName;

    @JsonView(value = {UserView.class, ListView.class, UserPositionInCompany.PositionView.class, Vote.VoteView.class})
    @Column
    private String middleName;

    @JsonView(value = {ListView.class, Question.QuestionView.class, Vote.VoteView.class})
    @Column
    private String lastName;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = {@JoinColumn(name = "user_id")})
    @Column(name = "roles")
    @Enumerated(EnumType.STRING)
    @JsonView(value = {ListView.class})
    private Set<UserRole> roles = new HashSet<>();

    @JsonView(value = {DetailView.class})
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    /**
     * todo User pic url or resource id from mongoDB
     */
    @JsonView(value = {DetailView.class})
    private String userPic;

    @JsonView(value = {DetailView.class})
    @Column
    private String phone;

    @JsonView(value = {DetailView.class})
    @Column
    private String email;

    @ManyToMany(mappedBy = "users")
    @JsonView(value = {DetailView.class})
    private Set<Committee> committees;

    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "userCompany.user", orphanRemoval = true)
    @JsonView(value = {DetailView.class})
    private List<UserPositionInCompany> positions = new ArrayList<UserPositionInCompany>();

    @OneToMany(mappedBy = "userQuestion.user")
    @JsonView(value = {DetailView.class})
    private List<UserVisitQuestion> userVisitQuestions = new ArrayList<>();

    public List<UserVisitQuestion> getUserVisitQuestions() {
        return userVisitQuestions;
    }

    public void setUserVisitQuestions(List<UserVisitQuestion> userVisitQuestions) {
        this.userVisitQuestions = userVisitQuestions;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    @JsonIgnore
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (UserRole role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }
        return authorities;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Committee> getCommittees() {
        return committees;
    }

    public void setCommittees(Set<Committee> committees) {
        this.committees = committees;
    }

    public List<UserPositionInCompany> getPositions() {
        return positions;
    }

    public void setPositions(List<UserPositionInCompany> positions) {
        this.positions = positions;
    }
}
