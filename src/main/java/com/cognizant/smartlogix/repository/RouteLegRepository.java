package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.RouteLeg;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RouteLegRepository extends JpaRepository<RouteLeg, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM RouteLeg r WHERE r.manifestId = :manifestId")
    void deleteByManifestId(Long manifestId);

    @Modifying
    @Transactional
    @Query("UPDATE RouteLeg r SET r.status = :status WHERE r.manifestId = :manifestId AND r.sequence = :sequence")
    void updateStatusByManifestAndSequence(Long manifestId, Integer sequence, String status);

    List<RouteLeg> findByManifestId(Long manifestId);
}