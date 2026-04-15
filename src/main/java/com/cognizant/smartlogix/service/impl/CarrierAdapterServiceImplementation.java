package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.OpsTrace.request.CarrierAdapterRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.CarrierAdapterResponseDTO;
import com.cognizant.smartlogix.exception.OpsTrace.CarrierAdapterNotFoundException;
import com.cognizant.smartlogix.model.CarrierAdapter;
import com.cognizant.smartlogix.repository.CarrierAdapterRepository;
import com.cognizant.smartlogix.service.CarrierAdapterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarrierAdapterServiceImplementation
        implements CarrierAdapterService {

    private final CarrierAdapterRepository repository;

    public CarrierAdapterServiceImplementation(CarrierAdapterRepository repository) {
        this.repository = repository;
    }

    @Override
    public CarrierAdapterResponseDTO create(CarrierAdapterRequestDTO dto) {

        CarrierAdapter adapter = new CarrierAdapter();
        adapter.setCarrierId(dto.carrierId());
        adapter.setProtocol(dto.protocol());
        adapter.setCredentialsJson(dto.credentialsJson());
        adapter.setStatus(dto.status());
        adapter.setLastSyncAt(null);

        return toResponse(repository.save(adapter));
    }

    @Override
    public List<CarrierAdapterResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CarrierAdapterResponseDTO getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    public CarrierAdapterResponseDTO update(Long id, CarrierAdapterRequestDTO dto) {

        CarrierAdapter adapter = find(id);
        adapter.setCarrierId(dto.carrierId());
        adapter.setProtocol(dto.protocol());
        adapter.setCredentialsJson(dto.credentialsJson());
        adapter.setStatus(dto.status());

        return toResponse(repository.save(adapter));
    }

    @Override
    public CarrierAdapterResponseDTO patch(Long id, CarrierAdapterRequestDTO dto) {

        CarrierAdapter adapter = find(id);

        if (dto.carrierId() != null)
            adapter.setCarrierId(dto.carrierId());
        if (dto.protocol() != null)
            adapter.setProtocol(dto.protocol());
        if (dto.credentialsJson() != null)
            adapter.setCredentialsJson(dto.credentialsJson());
        if (dto.status() != null)
            adapter.setStatus(dto.status());

        return toResponse(repository.save(adapter));
    }

    /**
     * ✅ Sync operation (PDF feature)
     */
    @Override
    public CarrierAdapterResponseDTO sync(Long id) {

        CarrierAdapter adapter = find(id);
        adapter.setLastSyncAt(LocalDateTime.now());

        return toResponse(repository.save(adapter));
    }

    @Override
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private CarrierAdapter find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CarrierAdapterNotFoundException(id));
    }

    private CarrierAdapterResponseDTO toResponse(CarrierAdapter adapter) {
        return new CarrierAdapterResponseDTO(
                adapter.getAdapterId(),
                adapter.getCarrierId(),
                adapter.getProtocol(),
                adapter.getCredentialsJson(),
                adapter.getLastSyncAt(),
                adapter.getStatus()
        );
    }
}
