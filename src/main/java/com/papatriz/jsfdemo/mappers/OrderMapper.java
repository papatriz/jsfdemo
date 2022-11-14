package com.papatriz.jsfdemo.mappers;

import com.papatriz.jsfdemo.dtos.OrderDTO;
import com.papatriz.jsfdemo.models.main.Node;
import com.papatriz.jsfdemo.models.main.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {

        OrderDTO oDTO = new OrderDTO();
        oDTO.setId(order.getId());
        oDTO.setComplete(order.isComplete());
        oDTO.setStatus(order.getStatus());
        oDTO.setAssignedTruck(order.getAssignedTruck().getRegNumber());
        Optional<Node> currentNode = order.getNodes().stream().filter(n -> !n.isComplete()).findFirst();
        String currCity = (currentNode.isEmpty())? "Finished" : currentNode.get().getCity().toString();
        oDTO.setCurrentCity(currCity);

        return oDTO;
    }
}
