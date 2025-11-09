package com.ecmsp.userservice.user.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RoleFacade {
    private final RoleRepository roleRepository;

    public RoleFacade(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleId createRole(RoleToCreate roleToCreate) {
        // Check if role with this name already exists
        Optional<Role> existingRole = roleRepository.findByName(roleToCreate.name());
        if (existingRole.isPresent()) {
            throw new IllegalArgumentException("Role with name '" + roleToCreate.name() + "' already exists");
        }

        RoleId roleId = new RoleId(UUID.randomUUID());
        Role role = new Role(roleId, roleToCreate.name(), roleToCreate.permissions());
        roleRepository.save(role);
        return roleId;
    }

    public void addPermissionToRole(RoleId roleId, Permission permission) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
        role.addPermission(permission);
        roleRepository.save(role);
    }

    public void removePermissionFromRole(RoleId roleId, Permission permission) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
        role.removePermission(permission);
        roleRepository.save(role);
    }

    public Optional<Role> findRoleById(RoleId roleId) {
        return roleRepository.findById(roleId);
    }

    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void deleteRole(RoleId roleId) {
        roleRepository.delete(roleId);
    }

    public void deleteRoleByName(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
        roleRepository.delete(role.id());
    }

    public void addPermissionToRoleByName(String roleName, Permission permission) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
        role.addPermission(permission);
        roleRepository.save(role);
    }

    public void removePermissionFromRoleByName(String roleName, Permission permission) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
        role.removePermission(permission);
        roleRepository.save(role);
    }
}
