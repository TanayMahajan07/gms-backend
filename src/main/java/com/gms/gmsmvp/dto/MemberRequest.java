package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

    @Size(max = 50)
    private String memberCode;

    private Long gymId;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    private Gender gender;

    @Past
    private LocalDate dateOfBirth;

    @Size(max = 15)
    @Pattern(regexp = "^[0-9+\\- ]*$", message = "mobile can contain only digits, spaces, + and -")
    private String mobile;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 500)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 150)
    private String emergencyContactName;

    @Size(max = 15)
    @Pattern(regexp = "^[0-9+\\- ]*$", message = "emergency contact can contain only digits, spaces, + and -")
    private String emergencyContactNumber;

    @NotNull
    private LocalDate joiningDate;

    @Size(max = 255)
    private String profilePhoto;

    private Boolean active;

    @Size(max = 500)
    private String remarks;

    private Long createdBy;

    private Long updatedBy;
}
