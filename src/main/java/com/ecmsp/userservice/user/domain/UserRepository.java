package com.ecmsp.userservice.user.domain;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UserId userId);
    Optional<User> findByLogin(String login);
}
