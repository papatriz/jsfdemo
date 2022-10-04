package com.papatriz.jsfdemo.models;


import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    // ----- SELF DATA -----
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "complete")
    private boolean isComplete;

    // ----- RELATED DATA -----
    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST})
    private List<Node> nodes;

    @OneToOne(mappedBy = "order")
    private Truck assignedTruck;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST})
    private List<Driver> drivers;

}
