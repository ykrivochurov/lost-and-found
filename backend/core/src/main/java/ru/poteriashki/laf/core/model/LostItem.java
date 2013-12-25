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

    private Set<String> tags;

    private boolean finished = false;

    private User author;

    private Set<String> photosIds;

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
}
