package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = Message.COLLECTION)
@JsonDeserialize
public class Message extends BaseEntity {

    public static final String COLLECTION = "message";

    private String itemOwner;

    private String nonOwner;

    private String sender;

    private String text;

    private String itemId;

    private Date creationDate;

    private boolean senderNew = true;

    private boolean receiverNew = true;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public boolean isSenderNew() {
        return senderNew;
    }

    public void setSenderNew(boolean senderNew) {
        this.senderNew = senderNew;
    }

    public boolean isReceiverNew() {
        return receiverNew;
    }

    public void setReceiverNew(boolean receiverNew) {
        this.receiverNew = receiverNew;
    }

    public String getItemOwner() {
        return itemOwner;
    }

    public void setItemOwner(String itemOwner) {
        this.itemOwner = itemOwner;
    }

    public String getNonOwner() {
        return nonOwner;
    }

    public void setNonOwner(String nonOwner) {
        this.nonOwner = nonOwner;
    }

    public Message clone() {
        Message message = new Message();
        message.setItemOwner(itemOwner);
        message.setNonOwner(nonOwner);
        message.setSender(sender);
        message.setCreationDate(creationDate);
        message.setItemId(itemId);
        message.setReceiverNew(receiverNew);
        message.setSenderNew(senderNew);
        message.setText(text);
        message.setId(getId());
        return message;
    }
}
