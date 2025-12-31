package com.ecmsp.userservice.user.domain;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UserId userId);
    Optional<User> findByLogin(String login);
    void deleteById(UserId userId);
    List<UserView> findAll();
    List<UserView> findByLoginContaining(String loginFilter);
    void updateLogin(UserId userId, String newLogin);
}
