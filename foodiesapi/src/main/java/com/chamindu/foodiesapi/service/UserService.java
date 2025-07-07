package com.chamindu.foodiesapi.service;

import com.chamindu.foodiesapi.io.UserRequest;
import com.chamindu.foodiesapi.io.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request);
    String findByUserId();
}
