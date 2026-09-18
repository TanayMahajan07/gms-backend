package com.gms.gmsmvp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

        @NotBlank
        @Size(min = 3, max = 100)
        private String username;

//        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8, max = 100)
        private String password;

        @NotBlank
        @Size(max = 100)
        private String firstName;

        @Size(max = 100)
        private String lastName;

        @Size(max = 15)
        private String mobile;
    }

