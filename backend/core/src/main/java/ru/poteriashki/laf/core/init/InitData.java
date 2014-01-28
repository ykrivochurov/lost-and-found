package ru.poteriashki.laf.core.init;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.repositories.CategoryRepository;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.repositories.MessageRepository;
import ru.poteriashki.laf.core.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Set;

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
    private ItemRepository itemRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @PostConstruct
    public void init() {
        Integer priority = 0;
        priority = createCategory(priority, "Животные", Sets.newHashSet(new String[]{"Кошки", "Собаки", "Другие животные"}));
        priority = createCategory(priority, "Документы", Sets.newHashSet(new String[]{"Документы", "Паспорт", "Водительское удостоверение", "Пенсионное удостоверение"}));
        priority = createCategory(priority, "Документы", Sets.newHashSet(new String[]{"Документы", "Паспорт", "Водительское удостоверение", "Пенсионное удостоверение"}));
        priority = createCategory(priority, "Деньги", Sets.newHashSet(new String[]{"Карта", "Кошелек"}));
        priority = createCategory(priority, "Рег. Номер", Sets.newHashSet(new String[]{"Рег. Номер"}));
        priority = createCategory(priority, "Ключи", Sets.newHashSet(new String[]{"Ключи от автомобиля", "Ключи от дома"}));
        priority = createCategory(priority, "Сумка", Sets.newHashSet(new String[]{"Сумка"}));
        priority = createCategory(priority, "Гаджеты", Sets.newHashSet(new String[]{"Мобильный телефон", "Флешкарта", "Планшет", "Ноутбук", "Плеер", "Видеокамера", "Фотоаппарат"}));
        priority = createCategory(priority, "Украшения", Sets.newHashSet(new String[]{"Сережки", "Кольцо", "Браслет", "Цепочка", "Кулон", "Часы"}));
        priority = createCategory(priority, "Другое", Sets.newHashSet(new String[]{}));
    }

    private Integer createCategory(Integer priority, String name, Set<String> tags) {
        Category category = categoryRepository.findOneByName(name);
        if (category == null) {
            category = new Category();
            category.setName(name);
            category.setTags(tags);
            category.setPriority(priority);
            categoryRepository.save(category);
            priority++;
        }
        return priority;
    }
}
