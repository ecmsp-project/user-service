package com.ecmsp.userservice.api.grpc.context;

import com.ecmsp.common.userservice.user.domain.Permission;
import org.springframework.stereotype.Component;

@Component
public class ContextAuthorization {
    public boolean hasPermission(UserContextData userContext, Permission permission) {
        return userContext.permissions().contains(permission);
    }

    public boolean isHimselfOrHasPermission(UserContextData userContext, String userId, Permission permission) {
        return userContext.userId().equals(userId) || hasPermission(userContext, permission);
    }
}
