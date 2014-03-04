package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.eastbanctech.resources.services.IResourceService;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.repositories.ItemRepository;
import ru.poteriashki.laf.core.service.ILostAndFoundService;
import ru.poteriashki.laf.core.service.ServiceException;
import ru.poteriashki.laf.web.security.UserContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/api/items")
public class ItemController {

    @Autowired
    private UserContext userContext;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private ILostAndFoundService lostAndFoundService;

    @Autowired
    private ItemRepository itemRepository;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Item create(@RequestBody Item item) throws InterruptedException, IOException,
            ru.eastbanctech.resources.services.ServiceException {
        return lostAndFoundService.createItem(item, userContext.getUser());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Item update(@RequestBody Item item) throws InterruptedException, IOException,
            ru.eastbanctech.resources.services.ServiceException, ServiceException {
        return lostAndFoundService.updateItem(item, userContext.getUser());
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<Item> all(@RequestParam("itemType") ItemType itemType, @RequestParam("category") String category,
                          @RequestParam(value = "tag", required = false) String tag, @RequestParam(value = "cityId") String cityId,
                          @RequestParam("pageNumber") Integer pageNumber) {
        return lostAndFoundService.getItems(itemType, category, tag, cityId, pageNumber, 100000); //todo paging
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Item changeCloseStatus(@PathVariable("id") String id) throws ServiceException {
        return lostAndFoundService.changeCloseStatus(id, userContext.getUser());
    }

    @RequestMapping(value = "/my", method = RequestMethod.GET)
    @ResponseBody
    public Page<Item> my(@RequestParam("pageNumber") Integer pageNumber) throws ServiceException {
        return lostAndFoundService.getMyItems(userContext.getUser(), pageNumber, 100000); //todo paging
    }

    @RequestMapping(value = "/{number}", method = RequestMethod.GET)
    @ResponseBody
    public Item one(@PathVariable("number") Integer number) throws ServiceException {
        return lostAndFoundService.loadByNumber(number, userContext.getUser());
    }

    @RequestMapping(value = "/markers", method = RequestMethod.GET)
    @ResponseBody
    public List<Item> markers(@RequestParam("itemType") ItemType itemType,
                              @RequestParam("cityId") String cityId) {
        return lostAndFoundService.getItemsForMarkers(itemType, cityId);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public List<Item> search(@RequestParam("query") String searchQuery, @RequestParam("itemType") ItemType itemType) {
        return lostAndFoundService.search(searchQuery, itemType);
    }

    @RequestMapping(value = "/photo", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    @ResponseBody
    public String uploadPhoto(@ModelAttribute MultipartFile fileData,
                              Model model, HttpServletResponse response) throws ServiceException, IOException,
            ru.eastbanctech.resources.services.ServiceException {
        return lostAndFoundService.createPhoto(fileData);
    }

    @RequestMapping(value = "/photo/{id}", method = RequestMethod.GET)
    public void loadDocument(@PathVariable("id") String id, @RequestParam(value = "w", required = false) Integer width,
                             @RequestParam(value = "h", required = false) Integer height, HttpServletResponse response)
            throws ServiceException, InterruptedException, IOException,
            ru.eastbanctech.resources.services.ServiceException {
        if (width != null && height != null) {
            resourceService.writeToResponse(id, 100, 100, response, false);
        } else {
            resourceService.writeToResponse(id, response);
        }
    }

}