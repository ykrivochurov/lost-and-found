package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Document(collection = Item.COLLECTION)
@JsonDeserialize
public class Item extends BaseEntity {

    public static final String COLLECTION = "items";

    private String what;

    private String where;

    private Date when;

    private Date creationDate;

    private Set<String> tags = new HashSet<>();

    private boolean finished = false;

    private String author;

    private Set<String> photosIds = new HashSet<>();

    private Double[] location; // lng, lat

    private boolean showPrivateInfo = false;

    private boolean money = false;

    private String mainCategory;

    private ItemType itemType;

    private String cityId;

    @Transient
    private User user;

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
