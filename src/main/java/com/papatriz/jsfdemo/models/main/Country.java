package com.papatriz.jsfdemo.models.main;

import java.util.HashMap;

public class Country implements ICountry{
    @Override
    public int getDistance(ECity from, ECity to) {
        return Math.abs(from.ordinal()- to.ordinal());
    }

    @Override
    public void addCity(ECity newCity, HashMap<ECity, Integer> neighbors) {

    }

    @Override
    public void removeCity(ECity removedCity) {

    }
}
