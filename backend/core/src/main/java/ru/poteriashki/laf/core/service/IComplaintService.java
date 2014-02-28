package ru.poteriashki.laf.core.service;

import ru.poteriashki.laf.core.model.Complaint;
import ru.poteriashki.laf.core.model.User;

public interface IComplaintService {

    Complaint create(Complaint complaint, User user);

}