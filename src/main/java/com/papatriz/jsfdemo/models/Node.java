package com.papatriz.jsfdemo.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private ECity city;
    @Enumerated(EnumType.STRING)
    private EOrderType type;

    @OneToOne()
    @JoinColumn(name = "cargo_id", referencedColumnName = "id")
    private Cargo cargo;

    @ManyToOne()
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}
