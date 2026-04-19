package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Return;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRepository extends JpaRepository<Return, Long> {

    List<Return> findByFulfillmentId(String fulfillmentId);
}