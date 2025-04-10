package com.chamindu.foodiesapi.service;

import com.chamindu.foodiesapi.entity.FoodEntity;
import com.chamindu.foodiesapi.io.FoodRequest;
import com.chamindu.foodiesapi.io.FoodResponse;
import com.chamindu.foodiesapi.repository.FoodRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Builder

public class FoodServiseImpl implements FoodService {

    private final CloudinaryClient cloudinaryClient;
    private final FoodRepository foodRepository;

    @Override
    public String uploadFile(MultipartFile file) {
        String fileNameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String key = UUID.randomUUID().toString() + "." + fileNameExtension;

        try {
            return cloudinaryClient.uploadFile(file, key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        FoodEntity newFoodEntity = converToEntity(request);
        String imageUrl = uploadFile(file);
        newFoodEntity.setImageUrl(imageUrl);
        newFoodEntity = foodRepository.save(newFoodEntity);
        return convertToResponse(newFoodEntity);
    }

    @Override
    public List<FoodResponse> readFoods() {
        List<FoodEntity> databaseEntities = foodRepository.findAll();
        return databaseEntities.stream().map(object -> convertToResponse(object)).collect(Collectors.toList());

    }

    @Override
    public FoodResponse readFood(String id) {
        FoodEntity exitingFood = foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found for id: " + id));
        return convertToResponse(exitingFood);

    }

    @Override
    public boolean deleteFile(String filename) {
        try {
            // Assuming CloudinaryClient has a deleteFile method
            return cloudinaryClient.deleteFile(filename);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + filename, e);
        }
    }

    @Override
    public void deleteFood(String id) {
        FoodEntity foodEntity = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));

        String imageUrl = foodEntity.getImageUrl();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf(".")); // Remove extension
        boolean isDeleted = deleteFile(fileName);

        if (isDeleted) {
            foodRepository.deleteById(id);
        } else {
            throw new RuntimeException("Failed to delete file from Cloudinary");
        }
    }

    private FoodEntity converToEntity(FoodRequest request) {
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    private FoodResponse convertToResponse(FoodEntity entity) {
        return FoodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
