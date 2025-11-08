package com.ecmsp.userservice.api.grpc;

import com.ecmsp.user.v1.RoleId;
import com.ecmsp.user.v1.UserId;
import com.ecmsp.userservice.user.domain.Permission;
import com.ecmsp.userservice.user.domain.Role;
import com.ecmsp.userservice.user.domain.UserView;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserGrpcMapper {

    public com.ecmsp.user.v1.User toProtoUser(UserView userView) {
        return com.ecmsp.user.v1.User.newBuilder()
                .setId(toProtoUserId(userView.id()))
                .setLogin(userView.login())
                .addAllRoles(userView.roles().stream()
                        .map(this::toProtoRole)
                        .collect(Collectors.toList()))
                .build();
    }

    public UserId toProtoUserId(com.ecmsp.userservice.user.domain.UserId domainUserId) {
        return UserId.newBuilder()
                .setValue(domainUserId.value().toString())
                .build();
    }

    public com.ecmsp.user.v1.Role toProtoRole(Role domainRole) {
        return com.ecmsp.user.v1.Role.newBuilder()
                .setId(toProtoRoleId(domainRole.id()))
                .setName(domainRole.name())
                .addAllPermissions(domainRole.permissions().stream()
                        .map(this::toProtoPermission)
                        .collect(Collectors.toList()))
                .build();
    }

    public RoleId toProtoRoleId(com.ecmsp.userservice.user.domain.RoleId domainRoleId) {
        return RoleId.newBuilder()
                .setValue(domainRoleId.value().toString())
                .build();
    }

    public String toProtoPermission(Permission permission) {
        return permission.name();
    }

    public com.ecmsp.userservice.user.domain.UserId toDomainUserId(UserId protoUserId) {
        return new com.ecmsp.userservice.user.domain.UserId(UUID.fromString(protoUserId.getValue()));
    }

    public com.ecmsp.userservice.user.domain.UserToCreate toDomainUserToCreate(com.ecmsp.user.v1.UserToCreate protoUserToCreate) {
        return new com.ecmsp.userservice.user.domain.UserToCreate(
                protoUserToCreate.getLogin(),
                protoUserToCreate.getPassword()
        );
    }
}
