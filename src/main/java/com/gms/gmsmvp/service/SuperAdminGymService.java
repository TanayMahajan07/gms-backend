package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.CreateGymWithAdminRequest;
import com.gms.gmsmvp.dto.CreateGymWithAdminResponse;
import com.gms.gmsmvp.entity.Gym;
import com.gms.gmsmvp.entity.GymSettings;
import com.gms.gmsmvp.entity.Role;
import com.gms.gmsmvp.entity.Users;
import com.gms.gmsmvp.repository.GymRepository;
import com.gms.gmsmvp.repository.GymSettingsRepository;
import com.gms.gmsmvp.repository.RoleRepository;
import com.gms.gmsmvp.repository.UserRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import com.gms.gmsmvp.security.SecurityUtils;
import com.gms.gmsmvp.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class SuperAdminGymService {

    private static final String ADMIN_ROLE = "ADMIN";

    private final GymRepository gymRepository;
    private final GymSettingsRepository gymSettingsRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateGymWithAdminResponse createGymWithAdmin(CreateGymWithAdminRequest request) {
        GmsUserDetails currentUser = SecurityUtils.requireCurrentUser();
        CreateGymWithAdminRequest.GymDetails gymDetails = request.getGym();
        CreateGymWithAdminRequest.AdminDetails adminDetails = request.getAdmin();

        String gymCode = gymDetails.getGymCode().trim();
        if (gymRepository.existsByGymCode(gymCode)) {
            throw new DuplicateResourceException("Gym code already exists: " + gymCode);
        }
        if (userRepository.existsByUsername(adminDetails.getUsername().trim())) {
            throw new DuplicateResourceException("Username already exists: " + adminDetails.getUsername());
        }
        if (userRepository.existsByEmail(adminDetails.getEmail().trim())) {
            throw new DuplicateResourceException("Email already exists: " + adminDetails.getEmail());
        }

        Role adminRole = roleRepository.findByRoleName(ADMIN_ROLE)
                .orElseThrow(() -> new IllegalStateException("ADMIN role is required in tbl_roles"));

        Gym gym = new Gym();
        gym.setGymCode(gymCode);
        gym.setGymName(gymDetails.getGymName().trim());
        gym.setAddress(trimToNull(gymDetails.getAddress()));
        gym.setCity(trimToNull(gymDetails.getCity()));
        gym.setState(trimToNull(gymDetails.getState()));
        gym.setPhone(trimToNull(gymDetails.getPhone()));
        gym.setEmail(trimToNull(gymDetails.getEmail()));
        gym.setCurrency(StringUtils.hasText(gymDetails.getCurrency()) ? gymDetails.getCurrency().trim() : "INR");
        gym.setDateFormat(StringUtils.hasText(gymDetails.getDateFormat()) ? gymDetails.getDateFormat().trim() : "dd-MM-yyyy");
        gym.setStatus("ACTIVE");
        gym.setCreatedBy(currentUser.getId());
        gym.setUpdatedBy(currentUser.getId());
        Gym savedGym = gymRepository.save(gym);

        GymSettings settings = new GymSettings();
        settings.setGymId(savedGym.getId());
        settings.setGymName(savedGym.getGymName());
        settings.setAddress(savedGym.getAddress());
        settings.setPhone(savedGym.getPhone());
        settings.setEmail(savedGym.getEmail());
        settings.setCurrency(savedGym.getCurrency());
        settings.setDateFormat(savedGym.getDateFormat());
        settings.setExpiryAlertThresholds("3,7,15,30");
        settings.setReceiptPrefix("RCPT");
        settings.setUpdatedBy(currentUser.getId());
        gymSettingsRepository.save(settings);

        Users admin = new Users();
        admin.setUsername(adminDetails.getUsername().trim());
        admin.setEmail(adminDetails.getEmail().trim());
        admin.setPasswordHash(passwordEncoder.encode(adminDetails.getPassword()));
        admin.setFirstName(adminDetails.getFirstName().trim());
        admin.setLastName(trimToNull(adminDetails.getLastName()));
        admin.setMobile(trimToNull(adminDetails.getMobile()));
        admin.setRole(adminRole);
        admin.setGymId(savedGym.getId());
        admin.setActive(true);
        admin.setCreatedBy(currentUser.getId());
        admin.setUpdatedBy(currentUser.getId());
        Users savedAdmin = userRepository.save(admin);

        String fullName = savedAdmin.getLastName() == null || savedAdmin.getLastName().isBlank()
                ? savedAdmin.getFirstName()
                : savedAdmin.getFirstName() + " " + savedAdmin.getLastName();

        return new CreateGymWithAdminResponse(
                savedGym.getId(),
                savedGym.getGymCode(),
                savedGym.getGymName(),
                savedGym.getStatus(),
                savedAdmin.getId(),
                savedAdmin.getUsername(),
                savedAdmin.getEmail(),
                fullName,
                savedAdmin.getRole().getRoleName()
        );
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
