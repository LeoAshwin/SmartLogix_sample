package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.pricing.ReturnCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.ReturnResponse;

import java.util.List;

public interface ReturnService {

    ReturnResponse createReturn(ReturnCreateRequest request);

    ReturnResponse getReturnById(Long returnId);

    List<ReturnResponse> getReturnsByFulfillmentId(String fulfillmentId);
}