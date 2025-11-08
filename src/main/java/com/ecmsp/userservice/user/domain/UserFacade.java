package com.ecmsp.userservice.user.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserFacade {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher = new PasswordHasher();

    public UserFacade(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    public void assignRoleToUser(UserId userId, RoleId roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        Set<Role> updatedRoles = new HashSet<>(user.roles());
        updatedRoles.add(role);
        User updatedUser = new User(user.id(), user.login(), user.passwordHash(), updatedRoles);
        userRepository.save(updatedUser);
    }

    public void removeRoleFromUser(UserId userId, RoleId roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Set<Role> updatedRoles = new HashSet<>(user.roles());
        updatedRoles.removeIf(role -> role.id().equals(roleId));
        User updatedUser = new User(user.id(), user.login(), user.passwordHash(), updatedRoles);
        userRepository.save(updatedUser);
    }

    public Set<Role> getUserRoles(UserId userId) {
        return userRepository.findById(userId)
                .map(User::roles)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
