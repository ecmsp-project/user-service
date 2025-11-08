package com.ecmsp.userservice.user.adapter.repository.inmemory;

import com.ecmsp.userservice.user.domain.User;
import com.ecmsp.userservice.user.domain.UserId;
import com.ecmsp.userservice.user.domain.UserRepository;
import com.ecmsp.userservice.user.domain.UserView;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.stream.Collectors;

class InMemoryUserRepository implements UserRepository {
    private final Map<UserId, User> users = new HashMap<>();

    public InMemoryUserRepository() {
        // Initialize with test user
        UserId testUserId = new UserId(UUID.fromString("3d1af02a-bba2-4df1-b551-e944bb60bc94"));
        User testUser = new User(testUserId, "testuser", BCrypt.hashpw("testpassword", BCrypt.gensalt()), Set.of());
        users.put(testUserId, testUser);
    }

    @Override
    public void save(User user) {
        users.put(user.id(), user);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        User user = users.get(userId);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values().stream()
                .filter(user -> user.login().equals(login))
                .findFirst();
    }

    @Override
    public void deleteById(UserId userId) {
        users.remove(userId);
    }

    @Override
    public List<UserView> findAll() {
        return users.values().stream()
                .map(user -> new UserView(user.id(), user.login(), user.roles()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserView> findByLoginContaining(String loginFilter) {
        return users.values().stream()
                .filter(user -> user.login().toLowerCase().contains(loginFilter.toLowerCase()))
                .map(user -> new UserView(user.id(), user.login(), user.roles()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateLogin(UserId userId, String newLogin) {
        User user = users.get(userId);
        if (user != null) {
            User updatedUser = new User(user.id(), newLogin, user.passwordHash(), user.roles());
            users.put(userId, updatedUser);
        }
    }
}
