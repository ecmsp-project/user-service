package com.ecmsp.userservice.api.grpc.context;

public record UserContextData(String userId,
                              String login) {
}
