package ru.poteriashki.laf.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.Chat;
import ru.poteriashki.laf.core.model.Message;
import ru.poteriashki.laf.core.service.IMessageService;
import ru.poteriashki.laf.core.service.ServiceException;
import ru.poteriashki.laf.web.security.UserContext;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/api/messages")
public class MessageController {

    @Autowired
    private UserContext userContext;

    @Autowired
    private IMessageService messageService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Message create(@RequestBody Message message) throws InterruptedException, ru.eastbanctech.resources.services.ServiceException, IOException {
        return messageService.create(message, userContext.getUser());
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<Chat> getChats(@RequestParam(value = "itemId", required = false) String itemId) throws ServiceException {
        if (StringUtils.isBlank(itemId)) {
            return messageService.getChats(userContext.getUser());
        } else {
            return messageService.getChatsByItemId(itemId, userContext.getUser());
        }
    }

    @RequestMapping(value = "/{chatId}", method = RequestMethod.GET)
    @ResponseBody
    public List<Message> getMessagesByChat(@PathVariable("chatId") String chatId) throws ServiceException {
        return messageService.getMessagesByChat(chatId, userContext.getUser());
    }

}