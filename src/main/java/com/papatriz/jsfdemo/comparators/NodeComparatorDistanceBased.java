package com.papatriz.jsfdemo.comparators;

import com.papatriz.jsfdemo.models.Country;
import com.papatriz.jsfdemo.models.ECity;
import com.papatriz.jsfdemo.models.ICountry;
import com.papatriz.jsfdemo.models.Node;

import java.util.Comparator;

public class NodeComparatorDistanceBased implements Comparator<Node> {
    private final ICountry country = new Country();
    private final ECity startCity;

    public NodeComparatorDistanceBased(ECity startCity) {
        this.startCity = startCity;
    }

    @Override
    public int compare(Node o1, Node o2) {
        if (o1.getCity() == o2.getCity()) return 0;
        int distance1 = country.getDistance(o1.getCity(), startCity);
        int distance2 = country.getDistance(o2.getCity(), startCity);
        return (distance1 > distance2) ? 1 : -1;
    }
}
