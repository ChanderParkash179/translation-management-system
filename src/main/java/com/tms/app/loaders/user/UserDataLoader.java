package com.tms.app.loaders.user;

import com.tms.app.entities.role.Role;
import com.tms.app.enums.RoleType;
import com.tms.app.repositories.role.RoleRepository;
import com.tms.app.services.role.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tms.app.entities.user.User;
import com.tms.app.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserDataLoader {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private User loadAdminUser() {
        log.info("loading admin user");
        return this.userRepository.findActiveUserByEmailOrUsername("admin@email.com").orElse(null);
    }

    @Bean
    @Order(2)
    public CommandLineRunner insertAdminUser() {
        return args -> {
            log.info("inserting admin user");
            User foundedUser = this.loadAdminUser();

            log.info("finding admin role");
            Role adminRole = roleRepository.findRoleByName(RoleType.ADMIN.name()).orElse(null);

            if (adminRole == null) {
                log.info("creating new admin role");
                adminRole = this.roleRepository.save(Role.builder().roleName("ADMIN").isActive(true).build());
            }

            if (foundedUser == null) {
                User admin = User.builder()
                        .username("admin")
                        .firstName("admin")
                        .lastName("admin")
                        .email("admin@email.com")
                        .password(passwordEncoder.encode("admin123"))
                        .isActive(true)
                        .role(adminRole)
                        .build();

                this.userRepository.save(admin);
                log.info("admin added successfully!");
            } else {
                log.info("admin user already exists!");
            }
        };
    }
}