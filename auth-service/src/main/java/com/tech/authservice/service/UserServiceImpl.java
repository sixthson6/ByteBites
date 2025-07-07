package com.tech.authservice.service;

import com.tech.authservice.dto.UserResponse;
import com.tech.authservice.dto.UserUpdateRequest;
import com.tech.authservice.exception.ResourceNotFoundException;
import com.tech.authservice.model.User;
import com.tech.authservice.model.enums.Role;
import com.tech.authservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.CUSTOMER));
        return userRepository.save(user);
    }

    @Override
    public User createAdminUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.ADMIN, Role.CUSTOMER));
        return userRepository.save(user);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        Optional.ofNullable(userUpdateRequest.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(userUpdateRequest.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(userUpdateRequest.getEmail()).ifPresent(user::setEmail);

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponse.class);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        userRepository.delete(user);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserResponse mapToDTO(User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public User mapToEntity(UserResponse userResponse) {
        return modelMapper.map(userResponse, User.class);
    }
}
