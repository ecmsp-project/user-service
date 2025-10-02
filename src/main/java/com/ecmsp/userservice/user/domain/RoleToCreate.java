package com.ecmsp.userservice.user.domain;

import java.util.Set;

public record RoleToCreate(String name, Set<Permission> permissions) {
}
