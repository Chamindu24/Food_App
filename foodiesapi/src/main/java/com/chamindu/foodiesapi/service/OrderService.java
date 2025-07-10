package com.chamindu.foodiesapi.service;

import com.chamindu.foodiesapi.io.OrderRequest;
import com.chamindu.foodiesapi.io.OrderResponse;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderResponse createOrderWithPayment(OrderRequest request) throws StripeException;
    void verifyPayment(Map<String, String> paymentData, String status);
    List<OrderResponse> getUserOrders();
    void removeOrder(String orderId);
    List<OrderResponse> getOrdersOfAllUsers();
    void updateOrderStatus(String orderId, String status);
}
