package ru.poteriashki.laf.service;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import ru.poteriashki.laf.config.TestPropHolderConfig;
import ru.poteriashki.laf.core.config.BaseConfiguration;
import ru.poteriashki.laf.core.service.ServiceException;

@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class})
public class UserServiceTest extends AbstractTestNGSpringContextTests {

    //    @Test(expectedExceptions = ServiceException.class)
    public void testCreate() throws ServiceException {
    }

}
