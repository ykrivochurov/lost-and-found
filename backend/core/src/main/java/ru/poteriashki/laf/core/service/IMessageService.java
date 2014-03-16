package ru.poteriashki.laf.core.service;

import org.springframework.data.domain.Page;
import ru.eastbanctech.resources.services.*;
import ru.poteriashki.laf.core.model.Chat;
import ru.poteriashki.laf.core.model.Message;
import ru.poteriashki.laf.core.model.User;

import java.io.IOException;
import java.util.List;

public interface IMessageService {

    Message create(Message message, User user) throws InterruptedException, IOException, ru.eastbanctech.resources.services.ServiceException;

    Page<Chat> getChats(User user);

    Page<Chat> getChatsByItemId(String itemId, User user);

    List<Message> getMessagesByChat(String chatId, User user);

}
