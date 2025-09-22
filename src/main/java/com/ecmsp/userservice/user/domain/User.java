package com.ecmsp.userservice.user.domain;

public record User(UserId id, String login, String passwordHash) {

}
