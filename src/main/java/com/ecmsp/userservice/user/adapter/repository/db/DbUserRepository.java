package com.ecmsp.userservice.user.adapter.repository.db;

import com.ecmsp.userservice.user.domain.User;
import com.ecmsp.userservice.user.domain.UserId;
import com.ecmsp.userservice.user.domain.UserRepository;

import java.util.Optional;

class DbUserRepository implements UserRepository {
    private final UserEntityMapper userMapper = new UserEntityMapper();
    private final UserEntityRepository userEntityRepository;

    DbUserRepository(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }


    @Override
    public void save(User user) {
        if(userEntityRepository.existsById(user.id().value())) {
            throw new RuntimeException("User already exists: " + user.id());
        }
        userEntityRepository.save(userMapper.toUserEntity(user));
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userEntityRepository.findById(userId.value())
                .map(userMapper::toUser);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userEntityRepository.findByLogin(login)
                .map(userMapper::toUser);
    }
}
