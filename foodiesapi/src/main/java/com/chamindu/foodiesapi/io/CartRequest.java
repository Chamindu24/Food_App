package com.chamindu.foodiesapi.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequest {
    private String userId;
    private Map<String, Integer> items = new HashMap<>();
}
