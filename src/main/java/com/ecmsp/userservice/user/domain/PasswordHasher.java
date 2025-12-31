package com.ecmsp.userservice.user.domain;

import org.mindrot.jbcrypt.BCrypt;

class PasswordHasher {
    String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


}
