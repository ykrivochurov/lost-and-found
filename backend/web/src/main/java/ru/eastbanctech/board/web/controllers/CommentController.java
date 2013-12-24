package ru.eastbanctech.board.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.eastbanctech.board.core.model.Comment;
import ru.eastbanctech.board.core.model.Question;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.ICommentService;
import ru.eastbanctech.board.core.service.ServiceException;
import ru.eastbanctech.board.web.config.jsonview.ResponseView;
import ru.eastbanctech.board.web.security.SecurityHelper;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * User: y.krivochurov
 * Date: 05.06.13
 * Time: 12:06
 */
@Controller
@RequestMapping(value = "/api/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseView(Question.QuestionView.class)
    @ResponseBody
    public Comment create(@RequestBody Comment comment) throws ServiceException {
        User user = SecurityHelper.getCurrentUser();

        return commentService.create(comment, user);
    }

    @RequestMapping(value = "/question/{id}", method = RequestMethod.GET)
    @ResponseView(Question.QuestionView.class)
    @ResponseBody
    public List<Comment> loadByQuestion(@PathVariable("id") Long questionId,
                                 @RequestParam("pageNumber") Integer pageNumber,
                                 @RequestParam("pageCount") Integer pageCount) throws ServiceException {
        User user = SecurityHelper.getCurrentUser();
        return commentService.loadByQuestion(questionId, new PageRequest(pageNumber, pageCount), user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) throws ServiceException {
        User user = SecurityHelper.getCurrentUser();
        commentService.delete(id, user);
    }
}
