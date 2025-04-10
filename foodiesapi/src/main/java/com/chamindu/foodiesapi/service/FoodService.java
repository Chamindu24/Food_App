package com.chamindu.foodiesapi.service;

import com.chamindu.foodiesapi.io.FoodRequest;
import com.chamindu.foodiesapi.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FoodService {
    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);
}
