package ru.eastbanctech.board.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.eastbanctech.board.core.dao.VoteRepository;
import ru.eastbanctech.board.core.model.Meeting;
import ru.eastbanctech.board.core.model.MeetingStatus;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.UserRole;
import ru.eastbanctech.board.core.model.Vote;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.IMeetingService;
import ru.eastbanctech.board.core.service.IQuestionService;
import ru.eastbanctech.board.core.service.IVotingService;
import ru.eastbanctech.board.core.service.ServiceException;

import java.util.Date;

/**
 * User: a.zhukov
 * Date: 14.06.13
 * Time: 17:40
 */
@Service
@Transactional
public class VotingService implements IVotingService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private IQuestionService questionService;

    @Override
    public void delete(Long id) throws ServiceException {
        Assert.notNull(id, "'id' parameter shouldn't be empty");

        voteRepository.delete(id);
    }

    @Override
    public Vote update(Vote vote) throws ServiceException {
        Assert.notNull(vote.getId(), "'vote.id' parameter shouldn't be empty");
        Assert.notNull(vote.getVoteState(), "'vote.voteState' parameter shouldn't be empty");

        Vote existingVote = voteRepository.findOne(vote.getId());
        if (existingVote == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Vote not found id=" + vote.getId());
        }

        existingVote.setVoteState(vote.getVoteState());
        return voteRepository.save(existingVote);
    }

    @Override
    public Vote create(Vote vote) throws ServiceException {
        Assert.notNull(vote, "'vote' parameter shouldn't be empty");
        Assert.notNull(vote.getUser(), "'vote.user' parameter shouldn't be empty");
        Assert.notNull(vote.getQuestion(), "'vote.question' parameter shouldn't be empty");
        Assert.notNull(vote.getQuestion().getId(), "'vote.question.id' parameter shouldn't be empty");
        Assert.notNull(vote.getVoteState(), "'vote.voteState' parameter shouldn't be empty");

        Question question = questionService.loadOne(vote.getQuestion().getId());

        Meeting meeting = meetingService.loadOne(question.getMeeting().getId());

        if (meeting.getStatus() != MeetingStatus.VOTING_OPENED) {
            throw new ServiceException(ErrorType.CONFLICT, "Voting is not open for meeting id="
                    + question.getMeeting().getId());
        }

        vote.setCreationDate(new Date());
        return voteRepository.save(vote);
    }

    @Override
    public Vote findById(Long id, User user) throws ServiceException {
        Assert.notNull(id, "'id' parameter shouldn't be empty");

        Vote vote = voteRepository.findOne(id);
        if (vote == null) {
            throw new ServiceException(ErrorType.OBJECT_NOT_FOUND, "Vote not found id=" + id);
        }

        if (user.getRoles().contains(UserRole.ROLE_SECRETARY)) {
            return vote;
        }

        if (!vote.getUser().equals(user)) {
            throw new ServiceException(ErrorType.ACCESS_DENIED, "Access only for vote`s author or secretary");
        }
        return vote;
    }
}
