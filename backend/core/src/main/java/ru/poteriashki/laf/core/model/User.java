package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = User.COLLECTION)
@JsonDeserialize
public class User extends BaseEntity {

    public static final String COLLECTION = "user";

    private String sid;

    private String uid;

    private String name;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String login;

    private String password;

    private UserType type;

    @Transient
    private Long itemsCount;

    @Transient
    private Long closedCount;

    @Transient
    private Favorite favorite;

    private Long newMessagesCount = 0l;

    private Long chatsCount = 0l;

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(Long itemsCount) {
        this.itemsCount = itemsCount;
    }

    public Long getClosedCount() {
        return closedCount;
    }

    public void setClosedCount(Long closedCount) {
        this.closedCount = closedCount;
    }

    public Long getNewMessagesCount() {
        return newMessagesCount;
    }

    public void setNewMessagesCount(Long newMessagesCount) {
        this.newMessagesCount = newMessagesCount;
    }

    public Long getChatsCount() {
        return chatsCount;
    }

    public void setChatsCount(Long chatsCount) {
        this.chatsCount = chatsCount;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    public void incChatsCount() {
        if (chatsCount == null) {
            chatsCount = 0l;
        }
        chatsCount++;
    }

    public void incNewMessagesCount() {
        if (newMessagesCount == null) {
            newMessagesCount = 0l;
        }
        newMessagesCount++;
    }
}