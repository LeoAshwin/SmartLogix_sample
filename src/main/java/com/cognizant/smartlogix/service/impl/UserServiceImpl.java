package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.UserMapper;
import com.cognizant.smartlogix.dto.request.CreateUserRequest;
import com.cognizant.smartlogix.dto.response.UserResponse;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.User;
import com.cognizant.smartlogix.model.enums.UserRole;
import com.cognizant.smartlogix.model.enums.UserStatus;
import com.cognizant.smartlogix.repository.UserRepository;
import com.cognizant.smartlogix.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper     = userMapper;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        // TODO: replace with BCryptPasswordEncoder.encode() when Spring Security is added
        String passwordHash = "[hashed]:" + request.getPassword();

        User user = User.builder()
                .name(request.getName())
                .role(request.getRole())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordHash)
                .mfaEnabled(request.getMfaEnabled() != null ? request.getMfaEnabled() : false)
                .status(UserStatus.ACTIVE)
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return userMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        return userMapper.toResponse(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "email", email)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).map(userMapper::toResponse);
    }

    @Override
    public UserResponse updateStatus(UUID id, UserStatus status) {
        User user = findEntityById(id);
        user.setStatus(status);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(findEntityById(id));
    }

    private User findEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}

