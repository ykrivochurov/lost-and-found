package ru.poteriashki.laf.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.eastbanctech.resources.services.ImageResourceInfo;
import ru.eastbanctech.resources.services.ServiceException;
import ru.eastbanctech.resources.services.impl.ResourceService;
import ru.poteriashki.laf.core.model.Chat;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.Message;
import ru.poteriashki.laf.core.model.TempResource;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.ChatRepository;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.repositories.MessageRepository;
import ru.poteriashki.laf.core.repositories.TempResourceRepository;
import ru.poteriashki.laf.core.repositories.UserRepository;
import ru.poteriashki.laf.core.service.IMessageService;
import ru.poteriashki.laf.core.service.IUserService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class MessageService implements IMessageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TempResourceRepository tempResourceRepository;

    @Autowired
    private ResourceService resourceService;

    @Override
    public Message create(Message message, User user) throws InterruptedException, IOException, ServiceException {
        Assert.notNull(user);
        Assert.notNull(message);
        Assert.hasText(message.getItemId());

        Item item = itemRepository.findOne(message.getItemId());
        User owner = userService.getById(item.getAuthor());

        // Чат начинает НЕ автор объявления
        Chat chat;
        if (message.getChatId() == null) {
            chat = new Chat();
            chat.setCount(1);
            chat.setItem(item);
            chat.setNonOwner(user);
            chat.setOwner(owner);
            chat.setNonOwnerNew(0);
            chat.setOwnerNew(1);
            owner.incNewMessagesCount();
        } else {
            chat = chatRepository.findOne(message.getChatId());
            chat.incCount();
            if (user.getId().equals(owner.getId())) {
                chat.incNonOwnerNew();
                chat.getNonOwner().incNewMessagesCount();
            } else {
                chat.incOwnerNew();
                chat.getOwner().incNewMessagesCount();
            }
        }
        chat.setModificationDate(new Date());
        chatRepository.save(chat);

        chat.getOwner().incChatsCount();
        if (!chat.getNonOwner().getId().equals(chat.getOwner().getId())) {
            chat.getNonOwner().incChatsCount();
        }
        userRepository.save(chat.getOwner());
        userRepository.save(chat.getNonOwner());

        message.setCreationDate(new Date());
        message.setSender(user.getId());
        message.setChatId(chat.getId());


        if (message.getPhotoId() != null) {
            ImageResourceInfo imageResourceInfo = resourceService.saveWithAnotherSize(message.getPhotoId(), 100, 100, false);
            message.setThumbnailId(imageResourceInfo.getId());
        }

        Message savedMessage = messageRepository.save(message);

        if (savedMessage.getPhotoId() != null) {
            TempResource tempResource = tempResourceRepository.findOneByFileId(savedMessage.getPhotoId());
            if (tempResource != null) {
                tempResourceRepository.delete(tempResource);
            }
        }

        return messageRepository.save(savedMessage);
    }

    @Override
    public Page<Chat> getChats(User user) {
        Assert.notNull(user);

        Page<Chat> chats = chatRepository.findByOwnerOrNonOwner(user, user,
                new PageRequest(0, 100000, new Sort(Sort.Direction.DESC, "modificationDate")));
        return chats;
    }

    @Override
    public Page<Chat> getChatsByItemId(String itemId, User user) {
        Assert.hasText(itemId);
        Assert.notNull(user);

        Item item = itemRepository.findOne(itemId);

        Page<Chat> chats = chatRepository.findByItemAndOwnerOrItemAndNonOwner(item, user, item, user,
                new PageRequest(0, 100000, new Sort(Sort.Direction.DESC, "modificationDate")));
        return chats;
    }

    @Override
    public List<Message> getMessagesByChat(String chatId, User user) {
        Assert.hasText(chatId);
        Assert.notNull(user);

        Chat chat = chatRepository.findOne(chatId);
        if (chat.getOwner().getId().equals(user.getId())) {
            chat.getOwner().setNewMessagesCount(chat.getOwner().getNewMessagesCount() - chat.getOwnerNew());
            chat.setOwnerNew(0);
        } else {
            chat.getNonOwner().setNewMessagesCount(chat.getNonOwner().getNewMessagesCount() - chat.getNonOwnerNew());
            chat.setNonOwnerNew(0);
        }
        chatRepository.save(chat);

        userRepository.save(chat.getOwner());
        userRepository.save(chat.getNonOwner());

        return messageRepository.findByChatId(chatId, new Sort(Sort.Direction.DESC, "creationDate"));
    }
}