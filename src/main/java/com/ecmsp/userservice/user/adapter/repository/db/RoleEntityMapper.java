package com.ecmsp.userservice.user.adapter.repository.db;

import com.ecmsp.userservice.user.domain.Role;
import com.ecmsp.userservice.user.domain.RoleId;

import java.util.Set;
import java.util.stream.Collectors;
import com.ecmsp.common.userservice.user.domain.Permission;

class RoleEntityMapper {

    public Role toRole(RoleEntity entity) {
        Set<Permission> permissions = entity.getPermissions().stream()
                .map(permissionEntity -> Permission.valueOf(permissionEntity.getPermissionName()))
                .collect(Collectors.toSet());

        return new Role(
                new RoleId(entity.getRoleId()),
                entity.getRoleName(),
                permissions
        );
    }

    public RoleEntity toRoleEntity(Role role) {
        Set<PermissionEntity> permissionEntities = role.permissions().stream()
                .map(permission -> PermissionEntity.builder()
                        .permissionName(permission.name())
                        .build())
                .collect(Collectors.toSet());

        return RoleEntity.builder()
                .roleId(role.id().value())
                .roleName(role.name())
                .permissions(permissionEntities)
                .build();
    }
}
