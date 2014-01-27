package ru.poteriashki.laf.core.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.CategoryRepository;
import ru.poteriashki.laf.core.repositories.FoundItemRepository;
import ru.poteriashki.laf.core.repositories.LostItemRepository;
import ru.poteriashki.laf.core.repositories.MessageRepository;
import ru.poteriashki.laf.core.repositories.UserRepository;

import javax.annotation.PostConstruct;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 15:51
 */
@Component
public class InitData {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoundItemRepository foundItemRepository;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @PostConstruct
    public void init() {
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setLogin("l1");
            user.setPassword("l1");
//            userRepository.save(user);
        }
    }
}
