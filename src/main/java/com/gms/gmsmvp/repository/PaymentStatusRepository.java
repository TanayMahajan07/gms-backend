package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {

    Optional<PaymentStatus> findByStatusCode(String statusCode);
}
