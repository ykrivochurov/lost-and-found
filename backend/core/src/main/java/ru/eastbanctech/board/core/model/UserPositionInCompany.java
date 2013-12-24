package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Hibernate;
import ru.eastbanctech.board.core.dtos.BaseView;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 13:09
 */
@Entity
@Table(name = "user_position_in_company")
@AssociationOverrides({
        @AssociationOverride(name = "userCompany.user", joinColumns = @JoinColumn(name = "c_user_id")),
        @AssociationOverride(name = "userCompany.company", joinColumns = @JoinColumn(name = "c_company_id"))
})
public class UserPositionInCompany implements Serializable {

    public interface PositionView extends BaseView {}

    @NotNull
    @EmbeddedId
    @JsonView(value = {PositionView.class})
    private UserCompany userCompany = new UserCompany();

    @Column(name = "position")
    @NotNull
    @JsonView(value = {PositionView.class})
    private String position;

    public UserCompany getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(UserCompany userCompany) {
        this.userCompany = userCompany;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setUser(User user) {
        getUserCompany().setUser(user);
    }

    public User getUser() {
        return getUserCompany().getUser();
    }

    public void setCompany(Company company) {
        getUserCompany().setCompany(company);
    }

    public Company getCompany() {
        return getUserCompany().getCompany();
    }

    @Embeddable
    public static class UserCompany implements Serializable {

        @NotNull
        @ManyToOne
        @JsonView(value = {PositionView.class})
        private User user;

        @NotNull
        @ManyToOne
        @JsonView(value = {PositionView.class})
        private Company company;

        public UserCompany() {
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Company getCompany() {
            return company;
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        @Override
        public final boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (other == null) {
                return false;
            }

            if (!Hibernate.getClass(other).equals(Hibernate.getClass(this))) {
                return false;
            }

            return getCompany().equals(((UserCompany) other).getCompany()) && getUser().equals(
                    ((UserCompany) other).getUser());
        }


        @Override
        public int hashCode() {
            int result = user != null ? user.hashCode() : 0;
            result = 31 * result + (company != null ? company.hashCode() : 0);
            return result;
        }
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (!Hibernate.getClass(other).equals(Hibernate.getClass(this))) {
            return false;
        }

        UserPositionInCompany that = (UserPositionInCompany) other;

        if (position != null ? !position.equals(that.position) : that.position != null) {
            return false;
        }
        if (userCompany != null ? !userCompany.equals(that.userCompany) : that.userCompany != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = userCompany != null ? userCompany.hashCode() : 0;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }
}
