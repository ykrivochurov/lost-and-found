package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.Message;
import ru.poteriashki.laf.core.service.IMessageService;
import ru.poteriashki.laf.core.service.ServiceException;
import ru.poteriashki.laf.web.security.UserContext;

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
    public Message create(@RequestBody Message message) {
        return messageService.create(message, userContext.getUser());
    }

    @RequestMapping(value = "/{itemId}", method = RequestMethod.GET)
    @ResponseBody
    public List<Message> allByItemId(@PathVariable("itemId") String itemId) throws ServiceException {
        return messageService.loadByItemId(itemId, userContext.getUser());
    }

}