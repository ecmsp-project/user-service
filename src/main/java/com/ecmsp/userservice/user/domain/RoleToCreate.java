package com.ecmsp.userservice.user.domain;

import com.ecmsp.common.userservice.user.domain.Permission;
import java.util.Set;

public record RoleToCreate(String name, Set<Permission> permissions) {
}
