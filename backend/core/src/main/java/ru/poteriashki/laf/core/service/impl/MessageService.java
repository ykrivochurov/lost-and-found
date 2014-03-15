package ru.poteriashki.laf.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.Message;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.repositories.MessageRepository;
import ru.poteriashki.laf.core.service.ErrorType;
import ru.poteriashki.laf.core.service.IMessageService;
import ru.poteriashki.laf.core.service.IUserService;
import ru.poteriashki.laf.core.service.ServiceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessageService implements IMessageService {

    @Autowired
    private IUserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message create(Message message, User user) {
        Assert.notNull(user);
        Assert.notNull(message);
        Assert.hasText(message.getItemId());

        Item item = itemRepository.findOne(message.getItemId());

        message.setCreationDate(new Date());
        message.setSender(user.getId());
        message.setItemOwner(item.getAuthor());

        return messageRepository.save(message);
    }

    @Override
    public List<User> nonOwners(String itemId, User user) throws ServiceException {
        Assert.hasText(itemId);
        Assert.notNull(user);

        Item item = itemRepository.findOne(itemId);
        if (item == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "");
        }

        if (!item.getAuthor().equals(user.getId())) {
            throw new ServiceException(ErrorType.ACCESS_DENIED, "");
        }

        List<User> users = new ArrayList<>();
        List<Message> messages = messageRepository.findByItemIdAndItemOwner(itemId, item.getAuthor(),
                new Sort(Sort.Direction.DESC, "creationDate"));
        for (Message message : messages) {
            User nonOwner = userService.getById(message.getNonOwner());
            if (!users.contains(nonOwner)) {
                users.add(nonOwner);
            }
        }

        return users;
    }

    @Override
    public List<Message> loadItemChat(String itemId, String nonOwner, User user) throws ServiceException {
        Assert.hasText(itemId);
        Assert.hasText(nonOwner);
        Assert.notNull(user);

        Item item = itemRepository.findOne(itemId);
        if (item == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "");
        }

        List<Message> messages = messageRepository.findByItemIdAndNonOwner(itemId, nonOwner,
                new Sort(Sort.Direction.DESC, "creationDate"));

/*
        List<Message> toUpdate = new ArrayList<>();
        for (Message message : messages) {
            Message clone = message.clone();
            clone.setReceiverNew(false);
            toUpdate.add(clone);
        }
        messageRepository.save(toUpdate);
*/

        return messages;
    }

}