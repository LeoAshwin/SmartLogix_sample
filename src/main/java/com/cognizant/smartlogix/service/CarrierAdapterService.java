package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.model.data.CarrierAdapter;

import java.util.List;
import java.util.Map;

public interface CarrierAdapterService {

    CarrierAdapter createAdapter(CarrierAdapter adapter);

    List<CarrierAdapter> getAllAdapters();

    CarrierAdapter getAdapterById(Long id);

    // ✅ Dummy sync
    Map<String, Object> syncAdapter(Long adapterId);
}