package com.ecmsp.userservice.user.domain;

import java.util.Optional;
import java.util.UUID;

public class UserFacade {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher = new PasswordHasher();

    public UserFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(UserToCreate userToCreate){
        UserId userId = new UserId(UUID.randomUUID());
        String hashedPassword = passwordHasher.hash(userToCreate.password());
        User user = new User(userId, userToCreate.login(), hashedPassword);
        userRepository.save(user);
    }

    public Optional<User> findUserById(UserId userId){
        return userRepository.findById(userId);
    }


    public Optional<User> findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }


}
