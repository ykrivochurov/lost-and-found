package ru.eastbanctech.board.core.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.eastbanctech.board.core.model.Committee;
import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.UserPositionInCompany;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 14:45
 */
public interface UserPositionInCompanyRepository extends CrudRepository<UserPositionInCompany, Long> {

    UserPositionInCompany findByUserCompanyUserIdAndUserCompanyCompanyId(Long userId, Long companyId);

    Page<UserPositionInCompany> findByUserCompanyCompanyId(Long companyId, Pageable pageable);

    @Query("select position from UserPositionInCompany position " +
            "join position.userCompany.user user " +
            "join user.committees committee " +
            "join committee.company company " +
            "where position.userCompany.company = company and committee = ?1 ")
    Page<UserPositionInCompany> findByCompanyIdAndCommitteeId(Committee committee, Pageable pageable);

}
