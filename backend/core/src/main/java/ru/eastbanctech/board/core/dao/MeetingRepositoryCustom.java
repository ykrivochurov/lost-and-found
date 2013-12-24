package ru.eastbanctech.board.core.dao;

import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.MeetingStatus;
import ru.eastbanctech.board.core.model.User;

import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 19.05.13
 * Time: 17:29
 */
public interface MeetingRepositoryCustom {

    List<Meeting> findByCompanyIn(User user, List<Company> companies, Date fromDate, Date toDate,
                                  List<MeetingStatus> statuses);

}
