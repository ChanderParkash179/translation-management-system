package com.tms.app.loaders.role;

import com.tms.app.entities.role.Role;
import com.tms.app.repositories.role.RoleRepository;
import com.tms.app.utils.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class RoleDataLoader {

    private final AppLogger log = new AppLogger(RoleDataLoader.class);

    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner insertRoles() {
        return args -> {
            List<Role> roles = List.of(
                    Role.builder().roleName("ADMIN").isActive(true).build(),
                    Role.builder().roleName("USER").isActive(true).build()
            );
            this.roleRepository.saveAll(roles);
            log.info("Roles inserted successfully!");
        };
    }
}