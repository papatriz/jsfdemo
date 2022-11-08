package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.main.ECity;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Scope(value = "application")
@Component(value = "citiesController")
@ELBeanName(value = "citiesController")
public class CitiesController {

    private List<String> cities;

    @PostConstruct
    private void generateList() {
        cities = Arrays.stream(ECity.values()).map(Enum::toString)
                .collect(Collectors.toList());
    }

    public List<String> getCities() {
        return cities;
    }
}
