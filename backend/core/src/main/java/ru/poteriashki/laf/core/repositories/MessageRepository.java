package ru.poteriashki.laf.core.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Message;

import java.util.List;

public interface MessageRepository extends PagingAndSortingRepository<Message, String> {

    List<Message> findByItemId(String itemId, Sort sort);

    List<Message> findByItemIdAndItemOwner(String itemId, String itemOwner, Sort sort);

    List<Message> findByItemIdAndNonOwner(String itemId, String nonOwner, Sort sort);

    List<Message> findByItemIdAndSenderOrReceiver(String itemId, String sender, String receiver, Sort sort);

    Long countByReceiverNewAnd(String itemId, String receiver, Boolean receiverNew);

    Long countByItemIdAndReceiverAndReceiverNew(String itemId, String receiver, Boolean receiverNew);

    Long countByItemIdAndReceiverOrItemIdAndSender(String itemId, String receiver, String sender);
}