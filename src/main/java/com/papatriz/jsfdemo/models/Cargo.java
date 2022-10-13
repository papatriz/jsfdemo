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

    @OneToOne(mappedBy = "cargo", fetch = FetchType.LAZY)
    private Node currentNode;

    @Override
    public String toString() {
        return "Cargo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cargo cargo = (Cargo) o;
        return id == cargo.id && weight == cargo.weight && name.equals(cargo.name) && status == cargo.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, weight, status);
    }
}