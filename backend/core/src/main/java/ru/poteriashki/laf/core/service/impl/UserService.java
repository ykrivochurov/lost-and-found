package ru.poteriashki.laf.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.model.UserType;
import ru.poteriashki.laf.core.repositories.UserRepository;
import ru.poteriashki.laf.core.service.IUserService;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getOrCreateUser(String uid, String sid, UserType userType) {
        Assert.hasText(uid);
        Assert.hasText(sid);
        Assert.notNull(userType);
        User user = userRepository.findOneByUidAndType(uid, userType);
        if (user == null) {
            user = new User();
            user.setUid(uid);
            user.setSid(sid);
            user.setType(userType);
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        Assert.notNull(user);
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getFirstName() + " " + user.getLastName());
        }
        return userRepository.save(user);
    }
}