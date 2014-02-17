package ru.poteriashki.laf.core.init;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.model.City;
import ru.poteriashki.laf.core.repositories.CategoryRepository;
import ru.poteriashki.laf.core.repositories.CityRepository;
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

    @Autowired
    private CityRepository cityRepository;

    @PostConstruct
    public void init() {
        City city = new City();
        city.setName("Новосибирск");
        city.setCenter(new Double[]{82.9203497573968, 55.030282009814});
        city.setLeftBottom(new Double[]{81.162537257397, 54.49227435248});
        city.setRightTop(new Double[]{84.678162257396, 55.561162429747});
        City existingCity = cityRepository.findOneByName(city.getName());
        if (existingCity == null) {
            cityRepository.save(city);
        }

        Integer priority = 0;
        priority = createCategory(priority, "Животные", Sets.newHashSet(new String[]{"Другие животные", "Кошки", "Собаки"}));
        priority = createCategory(priority, "Документы", Sets.newHashSet(new String[]{"Документы", "Паспорт", "Водительское удостоверение", "Пенсионное удостоверение"}));
        priority = createCategory(priority, "Деньги", Sets.newHashSet(new String[]{"Карта", "Кошелек"}));
        priority = createCategory(priority, "Рег. Номер", Sets.newHashSet(new String[]{"Рег. Номер"}));
        priority = createCategory(priority, "Ключи", Sets.newHashSet(new String[]{"Ключи от автомобиля", "Ключи от дома"}));
        priority = createCategory(priority, "Сумка", Sets.newHashSet(new String[]{"Сумка"}));
        priority = createCategory(priority, "Гаджеты", Sets.newHashSet(new String[]{"Мобильный телефон", "Флешкарта", "Планшет", "Ноутбук", "Плеер", "Видеокамера", "Фотоаппарат"}));
        priority = createCategory(priority, "Украшения", Sets.newHashSet(new String[]{"Сережки", "Кольцо", "Браслет", "Цепочка", "Кулон", "Часы"}));
        priority = createCategory(priority, "Другое", Sets.newHashSet(new String[]{"Другое"}));
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
