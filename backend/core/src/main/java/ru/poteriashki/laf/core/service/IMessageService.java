package ru.poteriashki.laf.core.service;

import ru.poteriashki.laf.core.model.Message;
import ru.poteriashki.laf.core.model.User;

import java.util.List;

public interface IMessageService {
    Message create(Message message, User user);

    List<Message> loadByItemId(String itemId, User user) throws ServiceException;
}
