package com.tms.app.services.role.Impl;

import com.tms.app.entities.role.Role;
import com.tms.app.repositories.role.RoleRepository;
import com.tms.app.services.role.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final Map<String, Role> roleCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadRoles() {
        log.info("loading roles");
        List<Role> roles = this.roleRepository.findAllActiveRoles();
        if (!roles.isEmpty()) {
            log.info("roles.size: {}", roles.size());
            roles.forEach(role -> roleCache.put(role.getRoleName(), role));
        } else
            log.error("no roles found in the database");
    }

    @Override
    public Role getRoleByName(String roleName) {
        log.info("getting role by name");
        return Optional.ofNullable(roleCache.get(roleName))
                .orElseThrow(() -> new RuntimeException("no role found for role " + roleName));
    }
}