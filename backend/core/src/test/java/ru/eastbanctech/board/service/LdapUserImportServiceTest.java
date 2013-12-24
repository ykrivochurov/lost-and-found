package ru.eastbanctech.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.eastbanctech.board.config.TestPersistenceJPAConfig;
import ru.eastbanctech.board.config.TestPropHolderConfig;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.service.ILdapUserImportService;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
@ContextConfiguration(classes = {TestPropHolderConfig.class, BaseConfiguration.class, TestPersistenceJPAConfig.class})
public class LdapUserImportServiceTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private ILdapUserImportService ldapUserImportService;

    /**
     * @verifies import users and return user count
     * @see ru.eastbanctech.board.core.service.ILdapUserImportService#importUsers()
     */
    @Test(enabled = true)
    public void importUsers_shouldImportUsersAndReturnUserCount() throws Exception {

        int count = ldapUserImportService.importUsers();

        Assert.assertNotEquals(0, count);
    }
}
