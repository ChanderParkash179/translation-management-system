package com.tms.app.repositories.role;

import com.tms.app.entities.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {


    @Query("SELECT r FROM Role r WHERE r.id = :id AND r.isActive = TRUE")
    Optional<Role> findRoleById(UUID id);

    @Query("SELECT r FROM Role r WHERE lower(r.roleName) = lower(:roleName) AND r.isActive = TRUE")
    Optional<Role> findRoleByName(String roleName);

    @Query("SELECT r FROM Role r WHERE r.isActive = TRUE ")
    List<Role> findAllRoles();
}