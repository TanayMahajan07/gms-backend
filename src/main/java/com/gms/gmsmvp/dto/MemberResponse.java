package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.entity.Gender;
import com.gms.gmsmvp.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String memberCode;
    private Long gymId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private LocalDate joiningDate;
    private String profilePhoto;
    private Boolean active;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getMemberCode(),
                member.getGymId(),
                member.getFirstName(),
                member.getLastName(),
                member.getGender(),
                member.getDateOfBirth(),
                member.getMobile(),
                member.getEmail(),
                member.getAddress(),
                member.getCity(),
                member.getEmergencyContactName(),
                member.getEmergencyContactNumber(),
                member.getJoiningDate(),
                member.getProfilePhoto(),
                member.getActive(),
                member.getRemarks(),
                member.getCreatedAt(),
                member.getUpdatedAt(),
                member.getCreatedBy(),
                member.getUpdatedBy()
        );
    }
}
