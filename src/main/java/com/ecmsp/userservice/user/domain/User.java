package com.ecmsp.userservice.user.domain;

import java.util.Set;

public record User(UserId id, String login, String passwordHash, Set<Role> roles) {

}
