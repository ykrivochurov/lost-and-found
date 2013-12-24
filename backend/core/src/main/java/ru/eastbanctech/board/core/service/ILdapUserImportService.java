package ru.eastbanctech.board.core.service;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
public interface ILdapUserImportService {

    /**
     * Import users.
     *
     * @return the int
     * @throws ServiceException the service exception
     * @should import users and return user count
     */
    int importUsers() throws ServiceException;
}
