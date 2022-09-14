package com.papatriz.jsfdemo.models;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;
    @Column
    private int weight;

    @Enumerated(EnumType.STRING)
    private ECargoStatus status;

    @OneToOne(mappedBy = "cargo")
    private Node currentNode;
}