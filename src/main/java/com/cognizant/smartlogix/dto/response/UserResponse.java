package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.UserRole;
import com.cognizant.smartlogix.model.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private UserRole role;
    private String email;
    private String phone;
    private Boolean mfaEnabled;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

