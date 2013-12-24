package ru.eastbanctech.board.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.eastbanctech.board.core.dao.UserPositionInCompanyRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IUserPositionInCompanyService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.core.service.ServiceException;

import java.util.List;

/**
 * User: a.zhukov
 * Date: 11.06.13
 * Time: 15:23
 */
@Service
@Transactional
public class UserPositionInCompanyService implements IUserPositionInCompanyService {

    @Autowired
    private UserPositionInCompanyRepository userPositionInCompanyRepository;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private ICommitteeService committeeService;

    @Autowired
    private IUserService userService;

    @Override
    public UserPositionInCompany create(UserPositionInCompany position) throws ServiceException {
        Assert.notNull(position, "'position' parameter shouldn't be empty");
        Assert.notNull(position.getPosition(), "'position.position' parameter shouldn't be empty");
        Long companyId = position.getCompany().getId();
        Long userId = position.getUser().getId();

        Company company = companyService.loadOne(companyId);
        User user = userService.loadOne(userId);

        position.setCompany(company);
        position.setUser(user);

        return userPositionInCompanyRepository.save(position);
    }

    @Override
    public UserPositionInCompany update(UserPositionInCompany position) throws ServiceException {
        Assert.notNull(position, "'position' parameter shouldn't be empty");
        Assert.notNull(position.getPosition(), "'position.position' parameter shouldn't be empty");
        Assert.notNull(position.getUser().getId(), "'position.user.id' parameter shouldn't be empty");
        Assert.notNull(position.getCompany().getId(), "'position.company.id' parameter shouldn't be empty");

        UserPositionInCompany existingPosition = getExistingPosition(position);
        existingPosition.setUser(position.getUser());
        existingPosition.setCompany(position.getCompany());
        existingPosition.setPosition(position.getPosition());
        return userPositionInCompanyRepository.save(existingPosition);
    }

    @Override
    public void delete(Long companyId, Long userId) throws ServiceException {
        Company company = companyService.loadOne(companyId);
        User user = userService.loadOne(userId);

        UserPositionInCompany position = new UserPositionInCompany();
        position.setCompany(company);
        position.setUser(user);

        UserPositionInCompany existingPosition = getExistingPosition(position);

        List<Committee> committees = committeeService.findByCompany(company.getId());
        for (Committee committee : committees) {
            committee.getUsers().remove(user);
            committeeService.update(committee);
        }

        userPositionInCompanyRepository.delete(existingPosition);
    }

    @Override
    public Page<UserPositionInCompany> findByCommitteeId(Long committeeId,
                                             Integer pageNumber, Integer pageCount) throws ServiceException {
        Assert.notNull(pageCount, "'pageCount' parameter shouldn't be empty");
        Assert.notNull(pageNumber, "'pageNumber' parameter shouldn't be empty");
        Committee committee = committeeService.loadOne(committeeId);
        return userPositionInCompanyRepository.findByCompanyIdAndCommitteeId(committee,
                new PageRequest(pageNumber, pageCount));
    }

    @Override
    public Page<UserPositionInCompany> findByCompanyId(Long companyId, Integer pageNumber,
                                                       Integer pageCount) throws ServiceException {
        Assert.notNull(pageCount, "'pageCount' parameter shouldn't be empty");
        Assert.notNull(pageNumber, "'pageNumber' parameter shouldn't be empty");
        Company company = companyService.loadOne(companyId);

        return userPositionInCompanyRepository.findByUserCompanyCompanyId(company.getId(),
                new PageRequest(pageNumber, pageCount));
    }

    private UserPositionInCompany getExistingPosition(UserPositionInCompany position) throws ServiceException {
        UserPositionInCompany existingPosition =
                userPositionInCompanyRepository.findByUserCompanyUserIdAndUserCompanyCompanyId(
                        position.getUser().getId(), position.getCompany().getId());
        if (existingPosition == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Position doesn't exist UserId=" +
                    position.getUser().getId() + " CompanyId=" + position.getCompany().getId());
        }
        return existingPosition;
    }
}
