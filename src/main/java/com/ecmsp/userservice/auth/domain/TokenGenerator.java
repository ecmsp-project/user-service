package com.ecmsp.userservice.auth.domain;

import com.ecmsp.userservice.user.domain.User;

public interface TokenGenerator {
    Token generate(User user);
}
