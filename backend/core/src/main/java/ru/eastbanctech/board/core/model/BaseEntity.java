package ru.eastbanctech.board.core.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Hibernate;
import ru.eastbanctech.board.core.dtos.BaseView;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 16:06
 */
@MappedSuperclass
public abstract class BaseEntity implements IEntity {

    public interface DetailView extends BaseView {}

    public interface ListView extends BaseView {}

    @Id
    @GeneratedValue
    @JsonView(value = {Vote.VoteView.class, User.UserView.class, Company.CompanyView.class,
            Vote.VoteView.class, Committee.CommitteeView.class, ListView.class, Meeting.MeetingDetailView.class,
            Question.QuestionView.class, UserPositionInCompany.PositionView.class})
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (!Hibernate.getClass(other).equals(Hibernate.getClass(this))) {
            return false;
        }

        return getId().equals(((BaseEntity) other).getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
