//package com.gms.gmsmvp.config;
//
//import com.gms.gmsmvp.entity.Role;
//import com.gms.gmsmvp.entity.Users;
//import com.gms.gmsmvp.repository.RoleRepository;
//import com.gms.gmsmvp.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Value("${app.default-admin.username}")
//    private String username;
//
//    @Value("${app.default-admin.email}")
//    private String email;
//
//    @Value("${app.default-admin.password}")
//    private String password;
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//
//        if (userRepository.existsByUsername(username)) {
//            return;
//        }
//
//        Role role = roleRepository.findByRoleName("ADMIN")
//                .orElseThrow(() -> new IllegalStateException("ADMIN role is required in tbl_roles"));
//
//        Users users = new Users();
//        users.setUsername(username);
//        users.setEmail(email);
//        users.setPasswordHash(passwordEncoder.encode(password));
//        users.setFirstName("Default");
//        users.setLastName("Admin");
//        users.setRole(role);
//        users.setGymId(1L);
//        users.setActive(true);
//
//        userRepository.save(users);
//    }
//}
