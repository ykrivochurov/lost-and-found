package ru.eastbanctech.board.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
@Controller
@RequestMapping("/")
public class BaseController {

    @RequestMapping(method = RequestMethod.GET)
    public String startView() {
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = "index")
    public String startIndexView() {
        return "templates/index";
    }

    @RequestMapping(value = "{viewName:.*}", method = RequestMethod.GET)
    public String jspView(@PathVariable("viewName") String viewName) {
        return viewName;
    }

}
