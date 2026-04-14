package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.CreateUserRequest;
import com.cognizant.smartlogix.dto.response.UserResponse;
import com.cognizant.smartlogix.model.enums.UserRole;
import com.cognizant.smartlogix.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponse create(CreateUserRequest request);
    UserResponse findById(UUID id);
    UserResponse findByEmail(String email);
    Page<UserResponse> findAll(Pageable pageable);
    Page<UserResponse> findByRole(UserRole role, Pageable pageable);
    UserResponse updateStatus(UUID id, UserStatus status);
    void delete(UUID id);
}

