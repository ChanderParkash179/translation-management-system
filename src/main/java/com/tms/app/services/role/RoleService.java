package com.tms.app.services.role;

import com.tms.app.entities.role.Role;

public interface RoleService {

    Role getRoleByName(String roleName);
}