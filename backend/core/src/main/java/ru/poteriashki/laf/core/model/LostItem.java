package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Document(collection = LostItem.COLLECTION)
@JsonDeserialize
public class LostItem extends BaseEntity {

    public static final String COLLECTION = "lost";

    private String what;

    private String where;

    private Date when;

    private Date creationDate;

    private Set<String> tags;

    private boolean finished = false;

    private User author;

    private Set<String> photosIds;

    private Double[] location;

    private boolean showPrivateInfo = false;

    private boolean money = false;

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<String> getPhotosIds() {
        return photosIds;
    }

    public void setPhotosIds(Set<String> photosIds) {
        this.photosIds = photosIds;
    }

    public Double[] getLocation() {
        return location;
    }

    public void setLocation(Double[] location) {
        this.location = location;
    }

    public boolean isShowPrivateInfo() {
        return showPrivateInfo;
    }

    public void setShowPrivateInfo(boolean showPrivateInfo) {
        this.showPrivateInfo = showPrivateInfo;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isMoney() {
        return money;
    }

    public void setMoney(boolean money) {
        this.money = money;
    }
}
