package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.service.ILostAndFoundService;
import ru.poteriashki.laf.web.security.UserContext;

import java.util.Collections;
import java.util.List;

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

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<Item> all(@RequestParam("itemType") ItemType itemType, @RequestParam("category") String category,
                          @RequestParam(value = "tag", required = false) String tag, @RequestParam(value = "cityId") String cityId,
                          @RequestParam("pageNumber") Integer pageNumber) {
        return lostAndFoundService.getItems(itemType, category, tag, cityId, pageNumber, 100000); //todo paging
    }

    @RequestMapping(value = "/markers", method = RequestMethod.GET)
    @ResponseBody
    public List<Item> markers(@RequestParam("itemType") ItemType itemType,
                              @RequestParam("cityId") String cityId) {
        return lostAndFoundService.getItemsForMarkers(itemType, cityId);
    }

}