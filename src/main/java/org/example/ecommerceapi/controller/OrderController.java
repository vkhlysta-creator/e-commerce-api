package org.example.ecommerceapi.controller;

import org.example.ecommerceapi.dto.OrderDto;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.checkout(user.getUsername()));
    }

    @GetMapping("")
    public ResponseEntity<List<OrderDto>> getAllOrders(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.getMyOrders(user.getUsername()));
    }


}
