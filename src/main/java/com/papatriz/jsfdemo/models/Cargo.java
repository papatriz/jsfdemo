package com.papatriz.jsfdemo.models;


import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cargo)) return false;
        Cargo cargo = (Cargo) o;
        return id == cargo.id && weight == cargo.weight && Objects.equals(name, cargo.name) && status == cargo.status && Objects.equals(currentNode, cargo.currentNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, weight, status, currentNode);
    }
}