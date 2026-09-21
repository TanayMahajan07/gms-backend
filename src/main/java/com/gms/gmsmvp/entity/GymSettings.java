package com.gms.gmsmvp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_gym_settings")
public class GymSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gym_id", unique = true)
    private Long gymId;

    @Column(name = "gym_name", nullable = false, length = 150)
    private String gymName;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "logo", length = 255)
    private String logo;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "date_format", nullable = false, length = 20)
    private String dateFormat = "dd-MM-yyyy";

    @Column(name = "expiry_alert_thresholds", nullable = false, length = 100)
    private String expiryAlertThresholds = "3,7,15,30";

    @Column(name = "receipt_prefix", length = 20)
    private String receiptPrefix = "RCPT";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;
}
