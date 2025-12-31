package com.ecmsp.userservice.api.rest.role;
import com.ecmsp.common.userservice.user.domain.Permission;

import java.util.Set;
import java.util.UUID;

public record RoleResponse(UUID id, String name, Set<Permission> permissions) {
}
