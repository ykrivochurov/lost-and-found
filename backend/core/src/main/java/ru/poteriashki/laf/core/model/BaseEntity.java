package ru.poteriashki.laf.core.model;

import org.hibernate.Hibernate;
import org.springframework.data.annotation.Id;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 16:06
 */
public abstract class BaseEntity implements IEntity {

    @Id
    public String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
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
