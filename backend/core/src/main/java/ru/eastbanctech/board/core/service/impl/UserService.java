package ru.eastbanctech.board.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.core.service.ServiceException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Override
    public User create(User user) throws ServiceException {
        Assert.notNull(user);

        User existingUser = userRepository.findByLogin(user.getLogin());
        if (existingUser != null) {
            throw new ServiceException(ErrorType.CONFLICT, "User with login \"" +
                    user.getLogin() + "\" already exists");
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getByCompanyNotIn(Long companyId) throws ServiceException {
        Company company = companyService.loadOne(companyId);
        List<User> allUsers = (List<User>) userRepository.findAll();
        allUsers.removeAll(userRepository.findByCompany(company));
        return allUsers;
    }

    @Override
    public List<User> getByCompanyInAndAllCompanyCommitteesNotIn(Long companyId) throws ServiceException {
        Company company = companyService.loadOne(companyId);
        List<User> users = userRepository.findByCompany(company);
        List<Committee> committees = committeeService.findByCompany(company.getId());
        for (Committee committee : committees) {
            users.removeAll(committee.getUsers());
        }
        return users;
    }

    @Override
    public User createOrUpdate(User user) {

        User dbUser = userRepository.findByLogin(user.getLogin());
        if (dbUser == null) {
            dbUser = userRepository.save(user);
        } else {
            dbUser.setEmail(user.getEmail());
            dbUser.setPhone(user.getPhone());
            dbUser.setFirstName(user.getFirstName());
            dbUser.setLastName(user.getLastName());
            userRepository.save(dbUser);
        }

        return dbUser;
    }

    @Override
    public User loadByName(String userName) {
        Assert.notNull(userName);

        return userRepository.findByLogin(userName);
    }

    @Override
    public User loadOne(Long id) throws ServiceException {
        Assert.notNull(id);

        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "User not found id=" + id);
        }
        return user;
    }
}
