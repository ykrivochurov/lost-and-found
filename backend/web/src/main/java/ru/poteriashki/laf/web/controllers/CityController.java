package ru.poteriashki.laf.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.poteriashki.laf.core.model.City;
import ru.poteriashki.laf.core.repositories.CityRepository;

@Controller
@RequestMapping(value = "/api/cities")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<City> all() {
        return cityRepository.findAll();
    }

}