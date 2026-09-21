package com.gms.gmsmvp.config;

import com.gms.gmsmvp.entity.Role;
import com.gms.gmsmvp.entity.Users;
import com.gms.gmsmvp.repository.RoleRepository;
import com.gms.gmsmvp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    private static final String LEGACY_ADMIN_USERNAME = "admin";
    private static final String LEGACY_ADMIN_EMAIL = "admin@gms.local";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-superadmin.username}")
    private String username;

    @Value("${app.default-superadmin.email}")
    private String email;

    @Value("${app.default-superadmin.password}")
    private String password;

    @Override
    @Transactional
    public void run(String... args) {
        removeLegacyDefaultAdmin();
        ensureDefaultSuperAdmin();
    }

    private void ensureDefaultSuperAdmin() {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        Role role = roleRepository.findByRoleName(SUPER_ADMIN_ROLE)
                .orElseThrow(() -> new IllegalStateException(
                        SUPER_ADMIN_ROLE + " role is required in tbl_roles"));

        Users users = new Users();
        users.setUsername(username);
        users.setEmail(email);
        users.setPasswordHash(passwordEncoder.encode(password));
        users.setFirstName("Super");
        users.setLastName("Admin");
        users.setRole(role);
        users.setGymId(null);
        users.setActive(true);

        userRepository.save(users);
    }

    private void removeLegacyDefaultAdmin() {
        if (LEGACY_ADMIN_USERNAME.equals(username)) {
            return;
        }

        userRepository.findByUsername(LEGACY_ADMIN_USERNAME).ifPresent(legacy -> {
            if (LEGACY_ADMIN_EMAIL.equalsIgnoreCase(legacy.getEmail())) {
                userRepository.delete(legacy);
            }
        });
    }
}
