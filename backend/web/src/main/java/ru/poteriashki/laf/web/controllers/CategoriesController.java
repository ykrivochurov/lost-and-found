package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.Category;
import ru.poteriashki.laf.core.service.ILostAndFoundService;

import java.util.Map;

@Controller
@RequestMapping(value = "/api/categories")
public class CategoriesController {

    @Autowired
    private ILostAndFoundService lostAndFoundService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Category> getAll() {
        return lostAndFoundService.getAllCategories();
    }

    @RequestMapping(value = "/counts", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Float> getCounts() {
        return lostAndFoundService.getCountsByTags();
    }

}