package ru.poteriashki.laf.core.service;

import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.model.UserType;

public interface IUserService {

    User getOrCreateUser(String uid, String sid, UserType userType);

    User updateUser(User user);

    User getById(String author);
}
