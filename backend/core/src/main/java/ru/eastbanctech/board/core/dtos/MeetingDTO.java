package ru.eastbanctech.board.core.dtos;

import ru.eastbanctech.board.core.model.Meeting;

/**
 * User: y.krivochurov
 * Date: 15.05.13
 * Time: 14:38
 */
public class MeetingDTO {

    private Meeting meeting;
    private Long questionsCount;
    private Long usersCount;

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Long getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(Long questionsCount) {
        this.questionsCount = questionsCount;
    }

    public Long getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(Long usersCount) {
        this.usersCount = usersCount;
    }
}
