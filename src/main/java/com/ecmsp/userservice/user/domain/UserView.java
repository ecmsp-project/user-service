package com.ecmsp.userservice.user.domain;

import java.util.Set;

public record UserView(UserId id, String login,  Set<Role> roles) {
}
