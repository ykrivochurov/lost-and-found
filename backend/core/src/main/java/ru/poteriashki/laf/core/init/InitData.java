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
import java.util.HashMap;
import java.util.Map;
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
        Map<String, Set<String>> catMap = new HashMap<>();
        catMap.put("Животные", Sets.newHashSet(new String[]{"Кошки", "Собаки", "Другие животные"}));
        catMap.put("Документы", Sets.newHashSet(new String[]{"Документы", "Паспорт", "Водительское удостоверение", "Пенсионное удостоверение"}));
        catMap.put("Деньги", Sets.newHashSet(new String[]{"Карта", "Кошелек"}));
        catMap.put("Рег. Номер", Sets.newHashSet(new String[]{"Рег. Номер"}));
        catMap.put("Ключи", Sets.newHashSet(new String[]{"Ключи от автомобиля", "Ключи от дома"}));
        catMap.put("Сумка", Sets.newHashSet(new String[]{"Сумка"}));
        catMap.put("Гаджеты", Sets.newHashSet(new String[]{"Мобильный телефон", "Флешкарта", "Планшет", "Ноутбук", "Плеер", "Видеокамера", "Фотоаппарат"}));
        catMap.put("Украшения", Sets.newHashSet(new String[]{"Сережки", "Кольцо", "Браслет", "Цепочка", "Кулон", "Часы"}));
        catMap.put("Другое", Sets.newHashSet(new String[]{}));
        for (Map.Entry<String, Set<String>> catMapEntry : catMap.entrySet()) {
            Category category = categoryRepository.findOneByName(catMapEntry.getKey());
            if (category == null) {
                category = new Category();
                category.setName(catMapEntry.getKey());
                category.setTags(catMapEntry.getValue());
                categoryRepository.save(category);
            }
        }
    }
}
