package com.ecmsp.userservice.api.rest.role;
import com.ecmsp.common.userservice.user.domain.Permission;

public record AddPermissionRequest(Permission permission) {
}
