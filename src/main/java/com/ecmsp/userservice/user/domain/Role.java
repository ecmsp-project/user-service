package com.ecmsp.userservice.user.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Role {
    private final RoleId id;
    private final String name;
    private final Set<Permission> permissions;

    public Role(RoleId id, String name, Set<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = new HashSet<>(permissions);
    }

    public RoleId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Set<Permission> permissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}
