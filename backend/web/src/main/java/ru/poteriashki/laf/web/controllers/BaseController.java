package ru.poteriashki.laf.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
