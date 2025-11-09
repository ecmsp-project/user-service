package com.ecmsp.userservice.api.grpc.context;


import com.ecmsp.userservice.user.domain.Permission;

import java.util.Set;

public record UserContextData(String userId,
                              String login,
                              Set<Permission> permissions) {
}
