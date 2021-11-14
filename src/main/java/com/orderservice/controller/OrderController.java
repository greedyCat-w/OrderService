package com.orderservice.controller;

import com.orderservice.model.Order;
import com.orderservice.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/getOrder/{customerName}/{orderId}")
    public Order getOrder(@PathVariable String customerName,@PathVariable String orderId){
        return orderRepository.getOrder(customerName,orderId);
    }
}
