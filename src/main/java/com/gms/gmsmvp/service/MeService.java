package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.GymResponse;
import com.gms.gmsmvp.dto.GymUpdateRequest;
import com.gms.gmsmvp.dto.ProfileResponse;
import com.gms.gmsmvp.dto.ProfileUpdateRequest;
import com.gms.gmsmvp.entity.Gym;
import com.gms.gmsmvp.entity.GymSettings;
import com.gms.gmsmvp.entity.Users;
import com.gms.gmsmvp.exception.ResourceNotFoundException;
import com.gms.gmsmvp.repository.GymRepository;
import com.gms.gmsmvp.repository.GymSettingsRepository;
import com.gms.gmsmvp.repository.UserRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import com.gms.gmsmvp.security.SecurityUtils;
import com.gms.gmsmvp.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class MeService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GymSettingsRepository gymSettingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        return ProfileResponse.from(loadCurrentUser());
    }

    public ProfileResponse updateProfile(ProfileUpdateRequest request) {
        Users user = loadCurrentUser();

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(trimToNull(request.getLastName()));

        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().trim().equalsIgnoreCase(user.getEmail())) {
            String email = request.getEmail().trim();
            if (userRepository.existsByEmail(email)) {
                throw new DuplicateResourceException("Email already exists: " + email);
            }
            user.setEmail(email);
        } else if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail().trim());
        }

        if (request.getMobile() != null) {
            user.setMobile(trimToNull(request.getMobile()));
        }

        boolean changingPassword = StringUtils.hasText(request.getNewPassword());
        if (changingPassword) {
            if (!StringUtils.hasText(request.getCurrentPassword())) {
                throw new IllegalArgumentException("Current password is required to set a new password");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        user.setUpdatedBy(user.getId());
        return ProfileResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public GymResponse getMyGym() {
        Long gymId = requireGymId();
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found: " + gymId));
        GymSettings settings = gymSettingsRepository.findByGymId(gymId).orElse(null);
        return GymResponse.from(gym, settings);
    }

    public GymResponse updateMyGym(GymUpdateRequest request) {
        GmsUserDetails currentUser = SecurityUtils.requireCurrentUser();
        Long gymId = requireGymId();

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found: " + gymId));

        gym.setGymName(request.getGymName().trim());
        gym.setAddress(trimToNull(request.getAddress()));
        gym.setCity(trimToNull(request.getCity()));
        gym.setState(trimToNull(request.getState()));
        gym.setPhone(trimToNull(request.getPhone()));
        gym.setEmail(trimToNull(request.getEmail()));
        gym.setLogo(trimToNull(request.getLogo()));
        if (StringUtils.hasText(request.getCurrency())) {
            gym.setCurrency(request.getCurrency().trim());
        }
        if (StringUtils.hasText(request.getDateFormat())) {
            gym.setDateFormat(request.getDateFormat().trim());
        }
        gym.setUpdatedBy(currentUser.getId());
        Gym savedGym = gymRepository.save(gym);

        GymSettings settings = gymSettingsRepository.findByGymId(gymId).orElseGet(() -> {
            GymSettings created = new GymSettings();
            created.setGymId(gymId);
            created.setExpiryAlertThresholds("3,7,15,30");
            created.setReceiptPrefix("RCPT");
            return created;
        });

        settings.setGymName(savedGym.getGymName());
        settings.setAddress(savedGym.getAddress());
        settings.setPhone(savedGym.getPhone());
        settings.setEmail(savedGym.getEmail());
        settings.setLogo(savedGym.getLogo());
        settings.setCurrency(savedGym.getCurrency());
        settings.setDateFormat(savedGym.getDateFormat());
        if (StringUtils.hasText(request.getExpiryAlertThresholds())) {
            settings.setExpiryAlertThresholds(request.getExpiryAlertThresholds().trim());
        }
        if (StringUtils.hasText(request.getReceiptPrefix())) {
            settings.setReceiptPrefix(request.getReceiptPrefix().trim());
        }
        settings.setUpdatedBy(currentUser.getId());
        GymSettings savedSettings = gymSettingsRepository.save(settings);

        return GymResponse.from(savedGym, savedSettings);
    }

    private Users loadCurrentUser() {
        GmsUserDetails principal = SecurityUtils.requireCurrentUser();
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));
    }

    private Long requireGymId() {
        GmsUserDetails principal = SecurityUtils.requireCurrentUser();
        if (principal.getGymId() == null) {
            throw new AccessDeniedException("No gym is linked to this account");
        }
        return principal.getGymId();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
