package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.Complaint;
import ru.poteriashki.laf.core.service.IComplaintService;
import ru.poteriashki.laf.web.security.UserContext;

import java.io.IOException;

@Controller
@RequestMapping(value = "/api/complaints")
public class ComplaintController {


    @Autowired
    private UserContext userContext;

    @Autowired
    private IComplaintService complaintService;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Complaint create(@RequestBody Complaint complaint) throws InterruptedException, IOException,
            ru.eastbanctech.resources.services.ServiceException {
        return complaintService.create(complaint, userContext.getUser());
    }

}