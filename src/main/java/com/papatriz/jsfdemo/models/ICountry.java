package com.papatriz.jsfdemo.models;

import java.util.HashMap;

public interface ICountry {

    int getDistance(ECity from, ECity to);
    void addCity(ECity newCity, HashMap<ECity, Integer> neighbors);
    void removeCity(ECity removedCity);
}