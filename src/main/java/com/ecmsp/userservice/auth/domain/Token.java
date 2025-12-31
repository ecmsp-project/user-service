package com.ecmsp.userservice.auth.domain;

public record Token(String value) {
    @Override
    public String toString() {
        return value;
    }
}
