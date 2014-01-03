package ru.poteriashki.laf.service;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.poteriashki.laf.config.TestPropHolderConfig;
import ru.poteriashki.laf.core.config.BaseConfiguration;
import ru.poteriashki.laf.core.service.ServiceException;

/**
 * User: a.zhukov
 * Date: 01.07.13
 * Time: 17:44
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class})
public class UserServiceTest extends AbstractTestNGSpringContextTests {

//    @Test(expectedExceptions = ServiceException.class)
    public void testCreate() throws ServiceException {
//        User user = userService.create(TestHelper.createUser());
//        user = userService.loadOne(user.getId());
//        userService.create(user);
    }

}
