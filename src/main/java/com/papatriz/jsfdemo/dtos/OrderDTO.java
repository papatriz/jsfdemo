package com.papatriz.jsfdemo.dtos;

import com.papatriz.jsfdemo.models.main.ECity;
import com.papatriz.jsfdemo.models.main.Truck;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDTO implements Serializable {
    private int id;
    private boolean isComplete;
    private String assignedTruck;
    private String status;
    private String currentCity;


}
