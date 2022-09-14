package com.papatriz.jsfdemo.models;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;

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


}
