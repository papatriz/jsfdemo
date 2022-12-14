package com.papatriz.jsfdemo.models.main;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@Entity

public class Truck {

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
        return regNumber+ " : "+driversNum+"dr. : "+capacity+"kg";
    }

    @OneToMany(mappedBy = "currentTruck")
    private List<Driver> assignedDrivers;

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
        boolean result = driversNum == truck.driversNum && capacity == truck.capacity && isBroken == truck.isBroken && Objects.equals(regNumber, truck.regNumber) && currentCity == truck.currentCity;
        System.out.println("EQUAL Truck method: "+result);
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(regNumber, driversNum, capacity, isBroken, currentCity);
    }
}
