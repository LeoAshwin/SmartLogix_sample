package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.UserResponse;
import com.cognizant.smartlogix.model.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserResponse toResponse(User user);
}

