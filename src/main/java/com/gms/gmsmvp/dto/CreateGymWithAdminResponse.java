package com.gms.gmsmvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGymWithAdminResponse {

    private Long gymId;
    private String gymCode;
    private String gymName;
    private String gymStatus;

    private Long adminId;
    private String adminUsername;
    private String adminEmail;
    private String adminFullName;
    private String adminRole;
}
