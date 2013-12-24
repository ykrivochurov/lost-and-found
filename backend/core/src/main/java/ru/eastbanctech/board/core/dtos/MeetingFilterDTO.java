package ru.eastbanctech.board.core.dtos;

import ru.eastbanctech.board.core.model.Company;
import ru.eastbanctech.board.core.model.MeetingStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 19.05.13
 * Time: 15:51
 */
public class MeetingFilterDTO {

    private List<MeetingStatus> statuses = new ArrayList<>();
    private List<Company> companies = new ArrayList<>();
    private Date fromDate;
    private Date toDate;

    public List<MeetingStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<MeetingStatus> statuses) {
        this.statuses = statuses;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
