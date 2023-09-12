package com.example.bookstore.service.role;

import com.example.bookstore.model.Role;

public interface RoleService {
    Role getRoleByRoleName(Role.RoleName roleName);
}
