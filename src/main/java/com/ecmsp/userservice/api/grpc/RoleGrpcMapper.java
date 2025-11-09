package com.ecmsp.userservice.api.grpc;

import com.ecmsp.userservice.user.domain.Permission;
import com.ecmsp.userservice.user.domain.RoleToCreate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleGrpcMapper {

    private final UserGrpcMapper userGrpcMapper;

    public RoleGrpcMapper(UserGrpcMapper userGrpcMapper) {
        this.userGrpcMapper = userGrpcMapper;
    }

    public Permission toDomainPermission(String permissionName) {
        return Permission.valueOf(permissionName);
    }

    public RoleToCreate toDomainRoleToCreate(com.ecmsp.user.v1.Role protoRole) {
        Set<Permission> permissions = protoRole.getPermissionsList().stream()
                .map(this::toDomainPermission)
                .collect(Collectors.toSet());

        return new RoleToCreate(protoRole.getName(), permissions);
    }

    // Delegate to UserGrpcMapper for proto conversions (reuse existing methods)
    public com.ecmsp.user.v1.Role toProtoRole(com.ecmsp.userservice.user.domain.Role domainRole) {
        return userGrpcMapper.toProtoRole(domainRole);
    }

    public String toProtoPermission(Permission permission) {
        return userGrpcMapper.toProtoPermission(permission);
    }
}
