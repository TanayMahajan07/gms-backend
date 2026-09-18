package com.gms.gmsmvp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
}
