package com.ecmsp.userservice.user.adapter.repository.db;

import com.ecmsp.userservice.user.domain.User;
import com.ecmsp.userservice.user.domain.UserId;
import com.ecmsp.userservice.user.domain.UserRepository;
import com.ecmsp.userservice.user.domain.UserView;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public void deleteById(UserId userId) {
        userEntityRepository.deleteById(userId.value());
    }

    @Override
    public List<UserView> findAll() {
        return userEntityRepository.findAll().stream()
                .map(userMapper::toUserView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserView> findByLoginContaining(String loginFilter) {
        return userEntityRepository.findByLoginContaining(loginFilter).stream()
                .map(userMapper::toUserView)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateLogin(UserId userId, String newLogin) {
        userEntityRepository.updateLogin(userId.value(), newLogin);
    }
}
