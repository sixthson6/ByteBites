package com.tech.authservice.dto;

import com.tech.authservice.model.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<Role> roles;
}
