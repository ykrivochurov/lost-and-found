package ru.poteriashki.laf.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.poteriashki.laf.core.model.Complaint;
import ru.poteriashki.laf.core.model.User;
import ru.poteriashki.laf.core.repositories.ComplaintRepository;
import ru.poteriashki.laf.core.service.IComplaintService;

import java.util.Date;

@Service
public class ComplaintService implements IComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Override
    public Complaint create(Complaint complaint, User user) {
        Assert.notNull(complaint);
        Assert.hasText(complaint.getItemId());

        complaint.setCreationDate(new Date());
        if (user != null) {
            complaint.setUserId(user.getId());
        }

        return complaintRepository.save(complaint);
    }

}