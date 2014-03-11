package ru.poteriashki.laf.core.service;

import ru.poteriashki.laf.core.model.Message;
import ru.poteriashki.laf.core.model.User;

import java.util.List;

public interface IMessageService {

    Message create(Message message, User user);

    Long countOfNewMessages(String itemId, User user);

    Long countOfMessages(String itemId, User user);

    List<User> nonOwners(String itemId, User user) throws ServiceException;

    List<Message> loadItemChat(String itemId, String nonOwner, User user) throws ServiceException;

}
