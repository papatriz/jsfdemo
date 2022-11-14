package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.dtos.OrderDTO;
import com.papatriz.jsfdemo.mappers.OrderMapper;
import com.papatriz.jsfdemo.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RestApiController {
    private final IOrderService orderService;
    private final OrderMapper orderMapper;
    @Autowired
    public RestApiController(IOrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("")
    public List<OrderDTO> simpleOrderList() {

        return orderService.getLastNOrders(10)
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }
}
