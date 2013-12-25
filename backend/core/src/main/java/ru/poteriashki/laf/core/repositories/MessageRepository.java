package ru.poteriashki.laf.core.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Message;

public interface MessageRepository extends PagingAndSortingRepository<Message, String> {
}
