package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.PricingRule;
import com.cognizant.smartlogix.model.enums.PricingRuleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, UUID> {

    Page<PricingRule> findByStatus(PricingRuleStatus status, Pageable pageable);

    @Query("""
           SELECT p FROM PricingRule p
           WHERE p.status = 'ACTIVE'
             AND p.effectiveFrom <= :date
             AND (p.effectiveTo IS NULL OR p.effectiveTo >= :date)
           ORDER BY p.priority ASC
           """)
    List<PricingRule> findActiveRulesForDate(@Param("date") LocalDate date);
}

