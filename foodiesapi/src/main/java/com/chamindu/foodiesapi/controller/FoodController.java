package com.chamindu.foodiesapi.controller;

import com.chamindu.foodiesapi.io.FoodRequest;
import com.chamindu.foodiesapi.io.FoodResponse;
import com.chamindu.foodiesapi.service.FoodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class FoodController {

    private static final Logger logger = LoggerFactory.getLogger(FoodController.class);
    private final FoodService foodService;

    @PostMapping
    public FoodResponse addFood(@RequestPart("food") String foodString,
                                @RequestPart("file") MultipartFile file) {
        logger.info("Received POST request to /api/foods");
        logger.debug("Food data: {}", foodString);
        logger.debug("File: name={}, size={}", file.getOriginalFilename(), file.getSize());

        ObjectMapper objectMapper = new ObjectMapper();
        FoodRequest foodRequest = null;
        try {
            foodRequest = objectMapper.readValue(foodString, FoodRequest.class);
            logger.info("Parsed FoodRequest: {}", foodRequest);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse food data: {}", foodString, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid food data", e);
        }

        FoodResponse foodResponse = foodService.addFood(foodRequest, file);
        logger.info("Food added successfully: {}", foodResponse);
        return foodResponse;
    }

    @GetMapping
    public List<FoodResponse> readFoods() {
        logger.info("Received GET request to /api/foods");
        List<FoodResponse> foods = foodService.readFoods();
        logger.info("Returning {} foods", foods.size());
        return foods;
    }

    @GetMapping("/{id}")
    public FoodResponse readFood(@PathVariable String id) {
        logger.info("Received GET request to /api/foods/{}", id);
        FoodResponse food = foodService.readFood(id);
        logger.info("Returning food: {}", food);
        return food;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(@PathVariable String id) {
        logger.info("Received DELETE request to /api/foods/{}", id);
        foodService.deleteFood(id);
        logger.info("Food with id {} deleted successfully", id);
    }
}