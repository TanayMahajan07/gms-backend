package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.GymSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymSettingsRepository extends JpaRepository<GymSettings, Long> {

    Optional<GymSettings> findByGymId(Long gymId);

    boolean existsByGymId(Long gymId);
}
