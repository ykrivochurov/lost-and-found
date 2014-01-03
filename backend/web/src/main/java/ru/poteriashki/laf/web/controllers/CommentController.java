package ru.poteriashki.laf.web.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User: y.krivochurov
 * Date: 05.06.13
 * Time: 12:06
 */
@Controller
@RequestMapping(value = "/api/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {
/*

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
*/
}
