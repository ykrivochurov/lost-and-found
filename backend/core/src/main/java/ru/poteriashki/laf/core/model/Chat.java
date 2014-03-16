package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = Chat.COLLECTION)
@JsonDeserialize
public class Chat extends BaseEntity {

    public static final String COLLECTION = "chat";

    @DBRef
    private Item item;

    @DBRef
    private User owner;

    @DBRef
    private User nonOwner;

    private Integer ownerNew;

    private Integer nonOwnerNew;

    private Integer count;

    private Date modificationDate;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getNonOwner() {
        return nonOwner;
    }

    public void setNonOwner(User nonOwner) {
        this.nonOwner = nonOwner;
    }

    public Integer getOwnerNew() {
        return ownerNew;
    }

    public void setOwnerNew(Integer ownerNew) {
        this.ownerNew = ownerNew;
    }

    public Integer getNonOwnerNew() {
        return nonOwnerNew;
    }

    public void setNonOwnerNew(Integer nonOwnerNew) {
        this.nonOwnerNew = nonOwnerNew;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void incCount() {
        if (count == null) {
            count = 0;
        }
        count++;
    }

    public void incOwnerNew() {
        if (ownerNew == null) {
            ownerNew = 0;
        }
        ownerNew++;
    }

    public void incNonOwnerNew() {
        if (nonOwnerNew == null) {
            nonOwnerNew = 0;
        }
        nonOwnerNew++;
    }
}