package com.ecmsp.userservice.user.adapter.repository.db;

import com.ecmsp.userservice.user.domain.User;
import com.ecmsp.userservice.user.domain.UserId;

class UserEntityMapper {

    public User toUser(UserEntity entity) {
        return new User(
                new UserId(entity.getUserId()),
                entity.getLogin(),
                entity.getPassword()
        );
    }

    public UserEntity toUserEntity(User user) {
        return UserEntity.builder()
                .userId(user.id().value())
                .login(user.login())
                .password(user.passwordHash())
                .build();
    }
}
