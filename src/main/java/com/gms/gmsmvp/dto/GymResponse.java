package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.entity.Gym;
import com.gms.gmsmvp.entity.GymSettings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GymResponse {

    private Long gymId;
    private String gymCode;
    private String gymName;
    private String address;
    private String city;
    private String state;
    private String phone;
    private String email;
    private String logo;
    private String currency;
    private String dateFormat;
    private String status;
    private String expiryAlertThresholds;
    private String receiptPrefix;

    public static GymResponse from(Gym gym, GymSettings settings) {
        return new GymResponse(
                gym.getId(),
                gym.getGymCode(),
                gym.getGymName(),
                gym.getAddress(),
                gym.getCity(),
                gym.getState(),
                gym.getPhone(),
                gym.getEmail(),
                gym.getLogo(),
                gym.getCurrency(),
                gym.getDateFormat(),
                gym.getStatus(),
                settings != null ? settings.getExpiryAlertThresholds() : "3,7,15,30",
                settings != null ? settings.getReceiptPrefix() : "RCPT"
        );
    }
}
