package ru.eastbanctech.board.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.eastbanctech.board.core.dao.CompanyRepository;
import ru.eastbanctech.board.core.dao.MeetingRepository;
import ru.eastbanctech.board.core.dao.UserPositionInCompanyRepository;
import ru.eastbanctech.board.core.dao.UserRepository;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.CompanyStatus;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserPositionInCompany;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.ICompanyService;
import ru.eastbanctech.board.core.service.ServiceException;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * User: y.krivochurov
 * Date: 04.05.13
 * Time: 15:43
 */
@Service
@Transactional
public class CompanyService implements ICompanyService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserPositionInCompanyRepository userPositionInCompanyRepository;

    @Override
    public Company create(Company company) throws ServiceException {

        Assert.notNull(company);

        Company existingCompany = companyRepository.findByName(company.getName());
        if (existingCompany != null) {
            throw new ServiceException(ErrorType.CONFLICT, "Company with name \"" +
                    company.getName() + "\" already exists");
        }

        return companyRepository.save(company);
    }

    @Override
    public Company update(Company company) throws ServiceException {

        Assert.notNull(company);

        Company companyWithSameName = companyRepository.findByName(company.getName());
        if (companyWithSameName != null) {
            throw new ServiceException(ErrorType.CONFLICT, "Company with name \"" +
                    company.getName() + "\" already exists");
        }

        Company existingCompany = companyRepository.findOne(company.getId());
        if (existingCompany == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Company doesn't exist id=" + company.getId());
        }

        existingCompany.setName(company.getName());
        return companyRepository.save(existingCompany);
    }

    @Override
    public void delete(Long companyId) throws ServiceException {

        Assert.notNull(companyId);

        Company company = getCompany(companyId);

        if (meetingRepository.findByCompany(company, new PageRequest(0, 1)).getNumberOfElements() > 0) {
            throw new ServiceException(ErrorType.CONFLICT, "Couldn't delete company with meetings");
        }
        companyRepository.delete(companyId);
    }

    @Override
    public boolean canDelete(Long companyId) throws ServiceException {
        Assert.notNull(companyId);
        Company company = getCompany(companyId);
        return (meetingRepository.findByCompany(company, new PageRequest(0, 1)).getNumberOfElements() == 0);
    }

    @Override
    public boolean canArchive(Long companyId) throws ServiceException {
        Assert.notNull(companyId);
        Company company = getCompany(companyId);
        return (meetingRepository.findNearestByCompany(company, new Date(),
                new PageRequest(0, 1)).getNumberOfElements() == 0);
    }

    // todo useless??
    @Override
    public UserPositionInCompany updateUserWithPosition(Long companyId, String position, Long userId) {

        Assert.notNull(companyId);
        Assert.notNull(position);
        Assert.notNull(userId);

        Company company = companyRepository.findOne(companyId);
        User user = userRepository.findOne(userId);

        UserPositionInCompany userPositionInCompany =
                userPositionInCompanyRepository.findByUserCompanyUserIdAndUserCompanyCompanyId(userId, companyId);

        if (userPositionInCompany == null) {
            userPositionInCompany = new UserPositionInCompany();
            userPositionInCompany.setCompany(company);
            userPositionInCompany.setUser(user);
        }
        userPositionInCompany.setPosition(position);
        userPositionInCompany = userPositionInCompanyRepository.save(userPositionInCompany);

        user.getPositions().add(userPositionInCompany);
        userRepository.save(user);

        company.getPositionInCompanies().add(userPositionInCompany);
        companyRepository.save(company);

        return userPositionInCompany;
    }

    @Override
    public Iterable<Company> loadAll() {

        return companyRepository.findAll();
    }

    @Override
    public Iterable<Company> loadActiveCompanies() {

        return companyRepository.loadAllActiveCompanies();
    }

    @Override
    public Iterable<Company> loadActiveCompaniesByUser(User user) {
        return companyRepository.loadAllActiveCompaniesByUser(user);
    }

    @Override
    @NotNull
    public Company loadOne(Long id) throws ServiceException {
        Company company = getCompany(id);
        company.setCanDelete(canDelete(id));
        company.setCanArchive(canArchive(id));
        return company;
    }

    @Override
    public Company changeStatus(Long companyId, CompanyStatus status) throws ServiceException {

        Assert.notNull(companyId);
        Assert.notNull(status);

        Company company = getCompany(companyId);

        if (company.getStatus().equals(CompanyStatus.ACTIVE) && status.equals(CompanyStatus.ARCHIVE)) {
            if (meetingRepository.findNearestByCompany(company, new Date(),
                    new PageRequest(0, 1)).getNumberOfElements() > 0) {
                throw new ServiceException(ErrorType.CONFLICT, "Couldn't archive company with planned meetings");
            }
        }

        company.setStatus(status);
        return companyRepository.save(company);
    }

    private Company getCompany(Long companyId) throws ServiceException {
        Assert.notNull(companyId);

        Company company = companyRepository.findOne(companyId);
        if (company == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Company not found id=" + companyId);
        }
        return company;
    }
}
