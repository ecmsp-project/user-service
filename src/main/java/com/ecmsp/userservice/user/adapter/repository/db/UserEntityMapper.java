package com.ecmsp.userservice.user.adapter.repository.db;

import com.ecmsp.userservice.user.domain.Role;
import com.ecmsp.userservice.user.domain.User;
import com.ecmsp.userservice.user.domain.UserId;
import com.ecmsp.userservice.user.domain.UserView;

import java.util.Set;
import java.util.stream.Collectors;

class UserEntityMapper {

    private final RoleEntityMapper roleEntityMapper;

    public UserEntityMapper() {
        this.roleEntityMapper = new RoleEntityMapper();
    }

    public User toUser(UserEntity entity) {
        Set<Role> roles = entity.getRoles().stream()
                .map(roleEntityMapper::toRole)
                .collect(Collectors.toSet());

        return new User(
                new UserId(entity.getUserId()),
                entity.getLogin(),
                entity.getPassword(),
                roles
        );
    }

    public UserView toUserView(UserEntity entity) {
        Set<Role> roles = entity.getRoles().stream()
                .map(roleEntityMapper::toRole)
                .collect(Collectors.toSet());

        return new UserView(
                new UserId(entity.getUserId()),
                entity.getLogin(),
                roles
        );
    }

    public UserEntity toUserEntity(User user) {
        Set<RoleEntity> roleEntities = user.roles().stream()
                .map(roleEntityMapper::toRoleEntity)
                .collect(Collectors.toSet());

        return UserEntity.builder()
                .userId(user.id().value())
                .login(user.login())
                .password(user.passwordHash())
                .roles(roleEntities)
                .build();
    }
}
