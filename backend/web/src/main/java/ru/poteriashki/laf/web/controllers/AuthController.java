package ru.poteriashki.laf.web.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.model.UserType;
import ru.poteriashki.laf.core.service.ILostAndFoundService;
import ru.poteriashki.laf.core.service.impl.UserService;
import ru.poteriashki.laf.web.security.UserContext;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String FB_USER_URL = "https://graph.facebook.com/%s?fields=first_name,last_name,email&access_token=%s";
    public static final String VK_USER_URL = "https://api.vk.com/method/users.get?user_id=%s&fields=contacts&v=5.7&access_token=%s";

    @Autowired
    private ILostAndFoundService lostAndFoundService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserContext userContext;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public User user() {
        User user = userContext.getUser();
        user = lostAndFoundService.itemsCountToUser(user);
        userContext.setUser(user);
        return user;
    }

    @RequestMapping(value = "/fb", method = RequestMethod.GET)
    @ResponseBody
    public User facebook(@RequestParam("token") String token, @RequestParam("uid") String uid) {
        User user = userService.getOrCreateUser(uid, token, UserType.FB);
        try {
            Document doc = Jsoup.connect(String.format(FB_USER_URL, uid, token)).ignoreContentType(true).get();
            Map<String, String> userMap = OBJECT_MAPPER.readValue(doc.text(), new TypeReference<Map<String, String>>() {
            });
            user.setFirstName(userMap.get("first_name"));
            user.setLastName(userMap.get("last_name"));
            user.setEmail(userMap.get("email"));
            user = userService.updateUser(user);
        } catch (Exception e) {
            LOGGER.debug("Unable to get user data", e);
        }
        user = lostAndFoundService.itemsCountToUser(user);
        userContext.setUser(user);
        return user;
    }

    @RequestMapping(value = "/vk", method = RequestMethod.GET)
    @ResponseBody
    public User vkontakte(@RequestParam("sid") String sid, @RequestParam("uid") String uid,
                          @RequestParam("name") String name) {
        User user = userService.getOrCreateUser(uid, sid, UserType.VK);
        try {
            Document doc = Jsoup.connect(String.format(VK_USER_URL, uid, sid)).ignoreContentType(true).get();
            Map<Object, Object> responseWrapper = OBJECT_MAPPER.readValue(doc.getElementsByTag("body").text(),
                    new TypeReference<Map<Object, Object>>() {
                    });
            Map<String, String> userMap = (Map<String, String>) ((List) responseWrapper.get("response")).get(0);
            user.setFirstName(userMap.get("first_name"));
            user.setLastName(userMap.get("last_name"));
            user.setEmail(userMap.get("email"));
            user.setPhone(userMap.get("home_phone"));
            userService.updateUser(user);
        } catch (Exception e) {
            LOGGER.debug("Unable to get user data", e);
        }
        user = lostAndFoundService.itemsCountToUser(user);
        userContext.setUser(user);
        return user;
    }


}
