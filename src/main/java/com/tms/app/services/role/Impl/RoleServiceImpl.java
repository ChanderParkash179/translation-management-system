package com.tms.app.services.role.Impl;

import com.tms.app.entities.role.Role;
import com.tms.app.repositories.role.RoleRepository;
import com.tms.app.services.role.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    private final Map<String, Role> roleCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadRoles() {
        this.roleRepository.findAllActiveRoles()
                .forEach(role -> roleCache.put(role.getRoleName(), role));
    }

    @Override
    public Role getRoleByName(String roleName) {
        return Optional.ofNullable(roleCache.get(roleName))
                .orElseThrow(() -> new RuntimeException("No Role Found"));
    }
}