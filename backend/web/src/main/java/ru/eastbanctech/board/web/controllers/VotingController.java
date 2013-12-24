package ru.eastbanctech.board.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.model.Vote;
import ru.eastbanctech.board.core.service.IVotingService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.web.config.jsonview.ResponseView;
import ru.eastbanctech.board.web.security.SecurityHelper;

import javax.servlet.http.HttpServletResponse;

/**
 * User: a.zhukov
 * Date: 14.06.13
 * Time: 17:37
 */
@Controller
@RequestMapping("/api/voting")
public class VotingController {

    @Autowired
    private IVotingService votingService;

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    @ResponseView(Vote.VoteView.class)
    Vote getVote(@PathVariable("id") Long id) throws JsonProcessingException, ServiceException {
        return votingService.findById(id, SecurityHelper.getCurrentUser());
    }

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) throws ServiceException {
        votingService.delete(id);
    }

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_USER.name())")
    @RequestMapping(method = RequestMethod.PUT)
    public
    @ResponseBody
    @ResponseView(Vote.VoteView.class)
    Vote create(@RequestBody Vote vote) throws JsonProcessingException, ServiceException {
        User user = SecurityHelper.getCurrentUser();
        vote.setUser(user);
        return votingService.create(vote);
    }

    @PreAuthorize("hasRole(T(ru.eastbanctech.board.core.model.UserRole).ROLE_SECRETARY.name())")
    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    @ResponseView(Vote.VoteView.class)
    Vote update(@RequestBody Vote vote) throws JsonProcessingException, ServiceException {
        return votingService.update(vote);
    }



}
