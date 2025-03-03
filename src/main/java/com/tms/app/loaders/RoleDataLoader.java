package com.tms.app.loaders;

import com.tms.app.entities.role.Role;
import com.tms.app.repositories.role.RoleRepository;
import com.tms.app.services.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RoleDataLoader {

    private final RoleRepository roleRepository;

    private final RedisService redisService;

    @Bean
    @Order(1)
    public CommandLineRunner insertRoles() {
        return args -> {

            this.redisService.clearAllCache();

            List<Role> roles = List.of(
                    Role.builder().roleName("ADMIN").isActive(true).build(),
                    Role.builder().roleName("USER").isActive(true).build()
            );

            List<Role> existingRoles = this.roleRepository.findActiveRolesByNames(
                    roles.stream()
                            .map(role -> role.getRoleName().toLowerCase())
                            .toList());

            List<Role> finalRoles = roles.stream()
                    .filter(role -> !existingRoles.stream()
                            .map(Role::getRoleName)
                            .toList()
                            .contains(role.getRoleName())
                    ).toList();

            if (!finalRoles.isEmpty())
                this.roleRepository.saveAll(finalRoles);
            log.info("Roles inserted successfully!");
        };
    }
}