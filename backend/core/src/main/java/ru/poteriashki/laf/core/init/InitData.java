package ru.poteriashki.laf.core.init;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.model.City;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.model.UserType;
import ru.poteriashki.laf.core.repositories.CategoryRepository;
import ru.poteriashki.laf.core.repositories.CityRepository;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.repositories.MessageRepository;
import ru.poteriashki.laf.core.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.List;

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
        priority = createCategory(priority, "Животные", Lists.newArrayList("Кошка", "Собака", "Другое животное"));
        priority = createCategory(priority, "Документы", Lists.newArrayList("Документы", "Паспорт", "Водительское удостоверение", "Пенсионное удостоверение"));
        priority = createCategory(priority, "Деньги", Lists.newArrayList("Карта", "Кошелек"));
        priority = createCategory(priority, "Рег. Номер", Lists.newArrayList("Рег. Номер"));
        priority = createCategory(priority, "Ключи", Lists.newArrayList("Ключи от автомобиля", "Ключи от дома"));
        priority = createCategory(priority, "Сумка", Lists.newArrayList("Сумка"));
        priority = createCategory(priority, "Гаджеты", Lists.newArrayList("Мобильный телефон", "Флешкарта", "Планшет", "Ноутбук", "Плеер", "Видеокамера", "Фотоаппарат"));
        priority = createCategory(priority, "Украшения", Lists.newArrayList("Сережки", "Кольцо", "Браслет", "Цепочка", "Кулон", "Часы"));
        priority = createCategory(priority, "Другое", Lists.newArrayList("Другое"));

        if (userRepository.findOneByUidAndType("tester1uid", UserType.VK) == null) {
            User user = new User();
            user.setName("Tester 1");
            user.setUid("tester1uid");
            user.setSid("tester1sid");
            user.setType(UserType.VK);
            user.setFirstName("Tester");
            user.setLastName("1");
            userRepository.save(user);
        }
        if (userRepository.findOneByUidAndType("tester2uid", UserType.VK) == null) {
            User user = new User();
            user.setName("Tester 2");
            user.setUid("tester2uid");
            user.setSid("tester2sid");
            user.setType(UserType.VK);
            user.setFirstName("Tester");
            user.setLastName("2");
            userRepository.save(user);
        }
        if (userRepository.findOneByUidAndType("tester3uid", UserType.VK) == null) {
            User user = new User();
            user.setName("Tester 3");
            user.setUid("tester3uid");
            user.setSid("tester3sid");
            user.setType(UserType.VK);
            user.setFirstName("Tester");
            user.setLastName("3");
            userRepository.save(user);
        }
    }

    private Integer createCategory(Integer priority, String name, List<String> tags) {
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
