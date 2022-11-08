package com.papatriz.jsfdemo.models.main;


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

}