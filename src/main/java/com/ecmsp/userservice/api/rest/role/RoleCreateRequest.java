package com.ecmsp.userservice.api.rest.role;

import com.ecmsp.userservice.user.domain.Permission;

import java.util.Set;

public record RoleCreateRequest(String name, Set<Permission> permissions) {
}
