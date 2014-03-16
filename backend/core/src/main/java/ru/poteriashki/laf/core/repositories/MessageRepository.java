package ru.poteriashki.laf.core.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Message;

import java.util.List;

public interface MessageRepository extends PagingAndSortingRepository<Message, String> {

    List<Message> findByChatId(String chatId, Sort sort);

}