package com.example.bookstore.service.role;

import com.example.bookstore.model.Role;
import com.example.bookstore.repository.role.RoleRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByRoleName(Role.RoleName roleName) {
        return roleRepository.findRoleByRoleName(roleName)
                .orElseThrow(() -> new NoSuchElementException("Can't find role: " + roleName));
    }
}
