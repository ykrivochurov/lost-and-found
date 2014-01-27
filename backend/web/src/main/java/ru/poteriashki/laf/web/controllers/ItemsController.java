package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.service.ILostAndFoundService;
import ru.poteriashki.laf.web.security.UserContext;

import java.util.Collections;

@Controller
@RequestMapping(value = "/api/items")
public class ItemsController {

    @Autowired
    private UserContext userContext;

    @Autowired
    private ILostAndFoundService lostAndFoundService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Item create(@RequestBody Item item) {
        return lostAndFoundService.createItem(item, userContext.getUser(), Collections.<String>emptySet());
    }

}