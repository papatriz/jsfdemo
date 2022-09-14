package com.papatriz.jsfdemo.models;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class  Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;
    @Column
    private String surname;

    @Column(name = "hours")
    private int hoursWorked;

    @Enumerated(EnumType.STRING)
    private EDriverStatus status;

    @Column(name = "city")
    private String currentCity;

    @ManyToOne()
    @JoinColumn(name="truck", referencedColumnName = "regnum", nullable=false)
    private Truck currentTruck;

    @ManyToOne()
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

}
