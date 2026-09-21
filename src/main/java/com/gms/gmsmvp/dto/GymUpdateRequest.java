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
public class GymUpdateRequest {

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

    @Email(message = "Enter a valid email")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    @Size(max = 255, message = "Logo URL must be at most 255 characters")
    @Pattern(regexp = ValidationPatterns.LOGO_URL, message = "Logo must be a valid http(s) URL")
    private String logo;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = ValidationPatterns.CURRENCY, message = "Currency must be INR, USD or EUR")
    private String currency;

    @NotBlank(message = "Date format is required")
    @Pattern(
            regexp = ValidationPatterns.DATE_FORMAT,
            message = "Date format must be dd-MM-yyyy, MM-dd-yyyy or yyyy-MM-dd"
    )
    private String dateFormat;

    @NotBlank(message = "Expiry alert thresholds are required")
    @Pattern(
            regexp = ValidationPatterns.EXPIRY_THRESHOLDS,
            message = "Use comma-separated days like 3,7,15,30"
    )
    private String expiryAlertThresholds;

    @NotBlank(message = "Receipt prefix is required")
    @Pattern(
            regexp = ValidationPatterns.RECEIPT_PREFIX,
            message = "Receipt prefix can contain letters, digits, _ or -"
    )
    private String receiptPrefix;
}
