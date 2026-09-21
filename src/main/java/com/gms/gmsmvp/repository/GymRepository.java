package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymRepository extends JpaRepository<Gym, Long> {

    boolean existsByGymCode(String gymCode);

    Optional<Gym> findByGymCode(String gymCode);
}
