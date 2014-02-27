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
import ru.poteriashki.laf.core.service.ServiceException;

import java.util.Date;
import java.util.List;

@Service
public class MessageService implements IMessageService {

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
        message.setReceiver(item.getAuthor());

        return messageRepository.save(message);
    }

    @Override
    public List<Message> loadByItemId(String itemId, User user) throws ServiceException {
        Assert.hasText(itemId);
        Assert.notNull(user);

        Item item = itemRepository.findOne(itemId);
        if (item == null || !item.getAuthor().equals(user.getId())) {
            throw new ServiceException(ErrorType.ACCESS_DENIED, "");
        }

        return messageRepository.findByItemId(itemId, new Sort(Sort.Direction.DESC, "creationDate"));
    }

}