package ru.poteriashki.laf.core.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.poteriashki.laf.core.model.Chat;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.User;

public interface ChatRepository extends PagingAndSortingRepository<Chat, String> {

    Page<Chat> findByOwnerOrNonOwner(User owner, User nonOwner, Pageable pageable);

    Page<Chat> findByItemAndOwnerOrItemAndNonOwner(Item item, User owner, Item itemAgain, User nonOwner, Pageable pageable);

}