package com.tech.authservice.service;

import com.tech.authservice.dto.UserResponse;
import com.tech.authservice.dto.UserUpdateRequest;
import com.tech.authservice.model.User;

public interface UserService {
    User createUser(User user);
    UserResponse getUserById(Long userId);
    User createAdminUser(User user);
    UserResponse getUserByEmail(String email);
    UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest);
    void deleteUser(Long userId);
    Boolean existsByEmail(String email);
    UserResponse mapToDTO(User user);
    User mapToEntity(UserResponse userResponse);
}
