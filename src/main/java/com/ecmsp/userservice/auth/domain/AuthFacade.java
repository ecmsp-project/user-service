package com.ecmsp.userservice.auth.domain;

import com.ecmsp.userservice.user.domain.UserFacade;
import org.mindrot.jbcrypt.BCrypt;

public class AuthFacade {
    private final UserFacade userFacade;
    private final TokenGenerator tokenGenerator;

    public AuthFacade(UserFacade userFacade, TokenGenerator tokenGenerator) {
        this.userFacade = userFacade;
        this.tokenGenerator = tokenGenerator;
    }

    public AuthenticationResult authenticate(String login, String password) {
        return userFacade.findUserByLogin(login)
                .map(user -> {
                    boolean passwordMatches = BCrypt.checkpw(password, user.passwordHash());
                    if (passwordMatches) {
                        return new AuthenticationResult.Success(tokenGenerator.generate(user));
                    }
                    return new AuthenticationResult.Failure("Invalid credentials");
                })
                .orElse(new AuthenticationResult.Failure("User not found"));
    }


}
