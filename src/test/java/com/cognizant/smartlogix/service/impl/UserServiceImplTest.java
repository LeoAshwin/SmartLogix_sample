package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.UserMapper;
import com.cognizant.smartlogix.dto.request.CreateUserRequest;
import com.cognizant.smartlogix.dto.response.UserResponse;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.model.entity.User;
import com.cognizant.smartlogix.model.enums.UserRole;
import com.cognizant.smartlogix.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        User mockUser = new User();
        UserResponse mockResponse = UserResponse.builder().build();

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        when(userMapper.toResponse(mockUser)).thenReturn(mockResponse);

        UserResponse result = userService.findById(id);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testCreate_DuplicateEmail_ThrowsException() {
        CreateUserRequest request = CreateUserRequest.builder().email("test@example.com").build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.create(request));
    }
}

