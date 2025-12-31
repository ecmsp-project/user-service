package com.ecmsp.userservice.api.grpc.context;


import java.util.Set;
import com.ecmsp.common.userservice.user.domain.Permission;

public record UserContextData(String userId,
                              String login,
                              Set<Permission> permissions) {
}
