package com.tms.app.repositories.role;

import com.tms.app.entities.role.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    @Query(value = "SELECT * FROM role WHERE lower(role_name) = lower(:roleName) AND is_active = TRUE", nativeQuery = true)
    Optional<Role> findRoleByName(@Param("roleName") String roleName);

    @Query(value = "SELECT * FROM role WHERE LOWER(role_name) IN (:roleNames) AND is_active = TRUE", nativeQuery = true)
    List<Role> findActiveRolesByNames(@Param("roleNames") List<String> roleNames);
}