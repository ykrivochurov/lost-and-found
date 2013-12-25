package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Message.COLLECTION)
@JsonDeserialize
public class Message extends BaseEntity {

    public static final String COLLECTION = "message";

    private User sender;

    private User receiver;

    private String title;

    private String text;

    private String lostOrFoundId;

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLostOrFoundId() {
        return lostOrFoundId;
    }

    public void setLostOrFoundId(String lostOrFoundId) {
        this.lostOrFoundId = lostOrFoundId;
    }
}
