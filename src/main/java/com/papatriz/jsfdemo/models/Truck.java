package com.papatriz.jsfdemo.models;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
public class Truck {
    //      private static final Logger logger = LoggerFactory.getLogger(Truck.class);

    @Id
    @Column(name = "regnum")
    private String regNumber; // XX12345

    @Column(name = "drivers")
    private int driversNum;

    @Column
    private int capacity;

    @Column(name = "isbroken")
    private boolean isBroken;

    @Enumerated(EnumType.STRING)
    @Column(name = "city")
    private ECity currentCity;

    @Override
    public String toString() {
        return "Truck : " + regNumber;
    }

    @OneToMany(mappedBy = "currentTruck")
    private Set<Driver> assignedDrivers;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    public boolean isAvailable()
    {
        return getStatus() == ETruckStatus.AVAILABLE;
    }

    public ETruckStatus getStatus(){

        if (this.isBroken)
            return ETruckStatus.BROKEN;
        else
            if (order == null)
                return ETruckStatus.AVAILABLE;

        return ETruckStatus.BUSY;
    }


    public Truck(String regNumber) {
        this.regNumber = regNumber;
    }

    public Truck() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Truck truck = (Truck) o;
        return driversNum == truck.driversNum && capacity == truck.capacity && isBroken == truck.isBroken && Objects.equals(regNumber, truck.regNumber) && currentCity == truck.currentCity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(regNumber, driversNum, capacity, isBroken, currentCity);
    }
}
