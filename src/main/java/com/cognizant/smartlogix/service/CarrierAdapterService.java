package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.OpsTrace.request.CarrierAdapterRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.CarrierAdapterResponseDTO;

import java.util.List;

public interface CarrierAdapterService {

    CarrierAdapterResponseDTO create(CarrierAdapterRequestDTO dto);

    List<CarrierAdapterResponseDTO> getAll();

    CarrierAdapterResponseDTO getById(Long id);

    CarrierAdapterResponseDTO update(Long id, CarrierAdapterRequestDTO dto);

    CarrierAdapterResponseDTO patch(Long id, CarrierAdapterRequestDTO dto);

    CarrierAdapterResponseDTO sync(Long id);

    void delete(Long id);
}
