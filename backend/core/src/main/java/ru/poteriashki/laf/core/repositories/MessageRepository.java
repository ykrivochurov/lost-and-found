package ru.poteriashki.laf.core.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Message;

import java.util.List;

public interface MessageRepository extends PagingAndSortingRepository<Message, String> {

    List<Message> findByItemIdAndItemOwner(String itemId, String itemOwner, Sort sort);

    List<Message> findByItemIdAndNonOwner(String itemId, String nonOwner, Sort sort);

}