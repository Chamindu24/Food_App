package com.chamindu.foodiesapi.service;

import com.chamindu.foodiesapi.io.OrderRequest;
import com.chamindu.foodiesapi.io.OrderResponse;
import com.chamindu.foodiesapi.repository.CartRepository;
import com.chamindu.foodiesapi.repository.OrderRepository;
import com.chamindu.foodiesapi.entity.OrderEntity;
import com.chamindu.foodiesapi.entity.OrderEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private  OrderRepository orderRepository;
    @Autowired
    private   UserService userService;
    @Autowired
    private CartRepository cartRepository;
    @Value("${Publishable.key}")
    private String STRIPE_PUBLISHABLE_KEY;
    @Value("${Secret.key}")
    private String STRIPE_SECRET_KEY;
    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) throws StripeException  {
        Stripe.apiKey = STRIPE_SECRET_KEY;
        OrderEntity newOrder = convertToEntity(request);
        newOrder.setUserId(userService.findByUserId()); // Set user ID before saving
        newOrder.setAmount(request.getAmount()); // Set amount from request
        newOrder.setPaymentStatus("pending"); // Set initial payment status
        //newOrder = orderRepository.save(newOrder);

        long amountInCents = (long) (newOrder.getAmount() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("lkr") // Use the correct currency code if LKR is not supported, fallback to "usd"
                .setDescription("Order for " + request.getEmail())
                .putMetadata("order_id", newOrder.getId())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        newOrder.setStripePaymentIntentId(paymentIntent.getId()); // Add this field to your entity
        newOrder.setUserId(userService.findByUserId());

        newOrder = orderRepository.save(newOrder);

        // Create response with clientSecret for frontend
        OrderResponse response = convertToResponse(newOrder);
        response.setClientSecret(paymentIntent.getClientSecret()); // Add this field

        return response;
    }

    @Override
    public void verifyPayment(Map<String, String> paymentData, String status) {

        String paymentIntentId = paymentData.get("payment_intent_id");

        OrderEntity existingOrder = orderRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Order not found for PaymentIntent ID: " + paymentIntentId));

        existingOrder.setPaymentStatus(status);
        orderRepository.save(existingOrder);

        if ("succeeded".equalsIgnoreCase(status)) {
            cartRepository.deleteByUserId(existingOrder.getUserId());
        }
    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUserId = userService.findByUserId();
        List<OrderEntity> list = orderRepository.findByUserId(loggedInUserId);
        return list.stream()
                .map(entity -> convertToResponse(entity))
                .collect(Collectors.toList());
    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderResponse> getOrdersOfAllUsers() {
        List<OrderEntity> list = orderRepository.findAll();
        return list.stream()
                .map(entity -> convertToResponse(entity))
                .collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        OrderEntity existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found for ID: " + orderId));
        existingOrder.setOrderStatus(status);
        orderRepository.save(existingOrder);
    }

    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .id(newOrder.getId())
                .amount(newOrder.getAmount())
                .userAddress(newOrder.getUserAddress())
                .userId(newOrder.getUserId())
                .stripePaymentIntentId(newOrder.getStripePaymentIntentId())
                .paymentStatus(newOrder.getPaymentStatus())
                .orderStatus(newOrder.getOrderStatus())
                .email(newOrder.getEmail())
                .phoneNumber(newOrder.getPhoneNumber())
                .orderedItems(newOrder.getItems())
                .build();
    }
    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userAddress(request.getUserAddress())
                .items(request.getOrderedItems())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .orderStatus(request.getOrderStatus())
                .build();
    }
}
