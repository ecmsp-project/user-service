package com.ecmsp.userservice.auth.domain;

public sealed interface AuthenticationResult {
    record Success(Token token) implements AuthenticationResult {}
    record Failure(String reason) implements AuthenticationResult {}
}
