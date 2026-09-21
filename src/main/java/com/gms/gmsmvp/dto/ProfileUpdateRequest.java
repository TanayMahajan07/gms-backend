package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.validation.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be at most 100 characters")
    @Pattern(regexp = ValidationPatterns.PERSON_NAME, message = "Enter a valid first name")
    private String firstName;

    @Size(max = 100, message = "Last name must be at most 100 characters")
    @Pattern(regexp = ValidationPatterns.PERSON_NAME_OPTIONAL, message = "Enter a valid last name")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    @Size(max = 15, message = "Mobile must be at most 15 characters")
    @Pattern(regexp = ValidationPatterns.PHONE, message = "Enter a valid mobile number")
    private String mobile;

    @Size(max = 100, message = "Current password must be at most 100 characters")
    private String currentPassword;

    @Size(max = 100, message = "New password must be at most 100 characters")
    @Pattern(
            regexp = ValidationPatterns.PASSWORD_OPTIONAL,
            message = "New password must be at least 8 characters and include a letter and a number"
    )
    private String newPassword;
}
