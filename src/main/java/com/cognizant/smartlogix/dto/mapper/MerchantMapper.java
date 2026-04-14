package com.cognizant.smartlogix.dto.mapper;

import com.cognizant.smartlogix.dto.response.MerchantResponse;
import com.cognizant.smartlogix.model.entity.Merchant;
import org.mapstruct.Mapper;

@Mapper
public interface MerchantMapper {
    MerchantResponse toResponse(Merchant merchant);
}

