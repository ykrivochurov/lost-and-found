package ru.eastbanctech.board.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.eastbanctech.board.core.dao.CommitteeRepository;
import ru.eastbanctech.board.core.dao.MeetingRepository;
import ru.eastbanctech.board.core.dao.UserPositionInCompanyRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.CommitteeStatus;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.ICommitteeService;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.core.service.ServiceException;

import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 04.05.13
 * Time: 16:46
 */
@Service
@Transactional
public class CommitteeService implements ICommitteeService {

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserPositionInCompanyRepository userPositionInCompanyRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Override
    public Committee create(Committee committee) throws ServiceException {
        Assert.notNull(committee);
        Assert.notNull(committee.getCompany());
        Assert.notNull(committee.getCompany().getId());
        Assert.notNull(committee.getName());

        Company company = companyService.loadOne(committee.getCompany().getId());
        for (Committee committee1 : company.getCommittees()) {
            if (committee.getName().equals(committee1.getName())) {
                throw new ServiceException(ErrorType.CONFLICT, "Committee with name "
                        + committee.getName() + " already exists in company " + committee.getCompany().getName());
            }
        }

        return committeeRepository.save(committee);
    }

    @Override
    public Committee update(Committee committee) throws ServiceException {
        Assert.notNull(committee);
        Assert.notNull(committee.getId());

        Committee existingCommittee = getCommittee(committee.getId());

        existingCommittee.setName(committee.getName());
        return committeeRepository.save(existingCommittee);
    }

    @Override
    public void addUser(Long userId, Long committeeId) throws ServiceException {
        Committee committee = getCommittee(committeeId);
        User user = userService.loadOne(userId);

        UserPositionInCompany position =
                userPositionInCompanyRepository.findByUserCompanyUserIdAndUserCompanyCompanyId(userId,
                        committee.getCompany().getId());
        if (position == null) {
            throw new ServiceException(ErrorType.CONFLICT, "User does not have a position in the company. userId="
                    + userId + " companyId=" + committee.getCompany().getId());
        }

        committee.getUsers().add(user);

        committeeRepository.save(committee);
    }

    @Override
    public void deleteUser(Long userId, Long committeeId) throws ServiceException {
        Committee committee = getCommittee(committeeId);
        User user = userService.loadOne(userId);


        committee.getUsers().remove(user);

        committeeRepository.save(committee);
    }

    @Override
    public List<Committee> findByCompany(Long companyId) throws ServiceException {
        Assert.notNull(companyId);

        return committeeRepository.findByCompanyId(companyId);
    }

    private Committee getCommittee(Long committeeId) throws ServiceException {
        Assert.notNull(committeeId);

        Committee committee = committeeRepository.findOne(committeeId);
        if (committee == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Committee not found id=" + committeeId);
        }
        return committee;
    }

    @Override
    public List<Committee> refreshCommittees(List<Committee> committees) throws ServiceException {
        Assert.notEmpty(committees);

        return committeeRepository.refreshCommittees(committees);
    }

    @Override
    public Committee changeStatus(Long committeeId, CommitteeStatus status) throws ServiceException {

        Assert.notNull(committeeId);
        Assert.notNull(status);

        Committee committee = getCommittee(committeeId);

        if (committee.getStatus().equals(CommitteeStatus.ACTIVE) && status.equals(CommitteeStatus.ARCHIVE)) {
            if (meetingRepository.findNearestByCommittee(committee, new Date(),
                    new PageRequest(0, 1)).getNumberOfElements() > 0) {
                throw new ServiceException(ErrorType.CONFLICT, "Couldn't archive committee with planned meetings");
            }
        }
        committee.setStatus(status);
        return update(committee);
    }

    @Override
    public void delete(Long id) throws ServiceException {
        Assert.notNull(id);
        Committee committee = getCommittee(id);

        if (meetingRepository.findByCommittee(committee, new PageRequest(0, 1)).getNumberOfElements() > 0) {
            throw new ServiceException(ErrorType.CONFLICT, "Couldn't delete committee with meetings");
        }
        committeeRepository.delete(id);
    }

    @Override
    public Committee loadOne(Long id) throws ServiceException {
        Committee committee = getCommittee(id);
        committee.setCanDelete(canDelete(id));
        committee.setCanArchive(canArchive(id));
        return committee;
    }

    @Override
    public boolean canDelete(Long committeeId) throws ServiceException {
        Assert.notNull(committeeId);
        Committee committee = getCommittee(committeeId);
        return (meetingRepository.findByCommittee(committee, new PageRequest(0, 1)).getNumberOfElements()== 0);
    }

    @Override
    public boolean canArchive(Long committeeId) throws ServiceException {
        Assert.notNull(committeeId);
        Committee committee = getCommittee(committeeId);
        return (meetingRepository.findNearestByCommittee(committee, new Date(),
                new PageRequest(0, 1)).getNumberOfElements() == 0);
    }
}
