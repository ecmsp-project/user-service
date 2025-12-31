package com.ecmsp.userservice.user.domain;
import com.ecmsp.userservice.user.adapter.repository.db.RoleEntity;
import com.ecmsp.userservice.user.adapter.repository.db.RoleEntityRepository;
import com.ecmsp.userservice.user.adapter.repository.db.UserEntity;
import com.ecmsp.userservice.user.adapter.repository.db.UserEntityRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserFacade {
    private final UserRepository userRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final PasswordHasher passwordHasher = new PasswordHasher();

    public UserFacade(UserRepository userRepository, RoleEntityRepository roleEntityRepository, UserEntityRepository userEntityRepository) {
        this.userRepository = userRepository;
        this.roleEntityRepository = roleEntityRepository;
        this.userEntityRepository = userEntityRepository;
    }

    public User createUser(UserToCreate userToCreate){
        UserId userId = new UserId(UUID.randomUUID());
        String hashedPassword = passwordHasher.hash(userToCreate.password());
        User user = new User(userId, userToCreate.login(), hashedPassword, new HashSet<>());
        userRepository.save(user);
        return user;
    }

    public Optional<User> findUserById(UserId userId){
        return userRepository.findById(userId);
    }

    public Optional<User> findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public void updateUserLogin(UserId userId, String newLogin) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        userRepository.updateLogin(userId, newLogin);
    }

    public void deleteUser(UserId userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        userRepository.deleteById(userId);
    }

    public List<UserView> listUsers(String filterLogin) {
        if (filterLogin == null || filterLogin.isBlank()) {
            return userRepository.findAll();
        }
        return userRepository.findByLoginContaining(filterLogin);
    }

    public void assignRoleToUser(UserId userId, String roleName) {
        UserEntity user = userEntityRepository.findById(userId.value())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        RoleEntity role = roleEntityRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.getRoles().add(role);
        userEntityRepository.save(user);
    }

    public void removeRoleFromUser(UserId userId, String roleName) {
        UserEntity user = userEntityRepository.findById(userId.value())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.getRoles().removeIf(role -> role.getRoleName().equals(roleName));
        userEntityRepository.save(user);
    }

    public Set<Role> getUserRoles(UserId userId) {
        return userRepository.findById(userId)
                .map(User::roles)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
