package com.gms.gmsmvp.dto;

import com.gms.gmsmvp.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String mobile;
    private String role;
    private Long gymId;

    public static ProfileResponse from(Users user) {
        String lastName = user.getLastName();
        String fullName = lastName == null || lastName.isBlank()
                ? user.getFirstName()
                : user.getFirstName() + " " + lastName;

        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                fullName,
                user.getMobile(),
                user.getRole().getRoleName(),
                user.getGymId()
        );
    }
}
