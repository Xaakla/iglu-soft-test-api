package com.iglusoft.api.controllers;

import com.iglusoft.api.dtos.DishOrderDto;
import com.iglusoft.api.dtos.OrderDishResponseDto;
import com.iglusoft.api.dtos.OrderResponseDto;
import com.iglusoft.api.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid List<DishOrderDto> order) {
        return ResponseEntity.ok(orderService.getOrderResponse(order));
    }
}
