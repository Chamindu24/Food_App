package com.chamindu.foodiesapi.controller;

import com.chamindu.foodiesapi.io.OrderRequest;
import com.chamindu.foodiesapi.io.OrderResponse;
import com.chamindu.foodiesapi.service.OrderService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request) throws StripeException {
        OrderResponse response = orderService.createOrderWithPayment(request);
        return response;

    }
    @PostMapping("/verify")
    public void verifyPayment(@RequestParam Map<String, String> paymentData) {
        orderService.verifyPayment(paymentData, "paid");
    }

    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderService.getUserOrders();
    }
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOrder(@PathVariable String orderId) {
        orderService.removeOrder(orderId);
    }

    @GetMapping("/all")
    public List<OrderResponse> getAllOrders() {
        return orderService.getOrdersOfAllUsers();
    }

    @PatchMapping("/status/{orderId}")
    public void updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
    }
}
