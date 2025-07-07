package com.chamindu.foodiesapi.service;

import com.chamindu.foodiesapi.io.CartRequest;
import com.chamindu.foodiesapi.io.CartResponse;

public interface CartService {
    CartResponse addToCart(CartRequest request);
    CartResponse getCart();
    void  clearCart();
    CartResponse removeFromCart(CartRequest request);
}
