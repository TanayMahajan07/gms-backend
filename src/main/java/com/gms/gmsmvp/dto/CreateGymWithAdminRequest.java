package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.validation.ValidationPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGymWithAdminRequest {

    @NotNull(message = "Gym details are required")
    @Valid
    private GymDetails gym;

    @NotNull(message = "Admin details are required")
    @Valid
    private AdminDetails admin;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GymDetails {

        @NotBlank(message = "Gym code is required")
        @Size(max = 50, message = "Gym code must be at most 50 characters")
        @Pattern(
                regexp = ValidationPatterns.GYM_CODE,
                message = "Gym code must start with a letter/digit and use only letters, digits, _ or -"
        )
        private String gymCode;

        @NotBlank(message = "Gym name is required")
        @Size(max = 150, message = "Gym name must be at most 150 characters")
        private String gymName;

        @Size(max = 500, message = "Address must be at most 500 characters")
        private String address;

        @Size(max = 100, message = "City must be at most 100 characters")
        private String city;

        @Size(max = 100, message = "State must be at most 100 characters")
        private String state;

        @Size(max = 15, message = "Phone must be at most 15 characters")
        @Pattern(regexp = ValidationPatterns.PHONE, message = "Enter a valid phone number")
        private String phone;

        @Email(message = "Enter a valid gym email")
        @Size(max = 150, message = "Email must be at most 150 characters")
        private String email;

        @Pattern(regexp = ValidationPatterns.CURRENCY, message = "Currency must be INR, USD or EUR")
        private String currency;

        @Pattern(
                regexp = ValidationPatterns.DATE_FORMAT,
                message = "Date format must be dd-MM-yyyy, MM-dd-yyyy or yyyy-MM-dd"
        )
        private String dateFormat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminDetails {

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 100, message = "Username must be 3 to 100 characters")
        @Pattern(
                regexp = ValidationPatterns.USERNAME,
                message = "Username can contain letters, digits, . _ - only"
        )
        private String username;

        @NotBlank(message = "Admin email is required")
        @Email(message = "Enter a valid admin email")
        @Size(max = 150, message = "Email must be at most 150 characters")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be 8 to 100 characters")
        @Pattern(
                regexp = ValidationPatterns.PASSWORD,
                message = "Password must be at least 8 characters and include a letter and a number"
        )
        private String password;

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must be at most 100 characters")
        @Pattern(regexp = ValidationPatterns.PERSON_NAME, message = "Enter a valid first name")
        private String firstName;

        @Size(max = 100, message = "Last name must be at most 100 characters")
        @Pattern(regexp = ValidationPatterns.PERSON_NAME_OPTIONAL, message = "Enter a valid last name")
        private String lastName;

        @Size(max = 15, message = "Mobile must be at most 15 characters")
        @Pattern(regexp = ValidationPatterns.PHONE, message = "Enter a valid mobile number")
        private String mobile;
    }
}
