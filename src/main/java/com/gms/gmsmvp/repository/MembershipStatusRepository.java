package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipStatusRepository extends JpaRepository<MembershipStatus, Long> {

    Optional<MembershipStatus> findByStatusCode(String statusCode);
}
