package com.ecmsp.userservice.user.adapter.repository.inmemory;

import com.ecmsp.userservice.user.domain.User;
import com.ecmsp.userservice.user.domain.UserId;
import com.ecmsp.userservice.user.domain.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemoryUserRepository implements UserRepository {
    private final Map<UserId, User> users = Map.of(
            new UserId(UUID.fromString("3d1af02a-bba2-4df1-b551-e944bb60bc94")),
            new User(new UserId(UUID.fromString("3d1af02a-bba2-4df1-b551-e944bb60bc94")), "testuser", BCrypt.hashpw("testpassword", BCrypt.gensalt()))
    );

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


}
