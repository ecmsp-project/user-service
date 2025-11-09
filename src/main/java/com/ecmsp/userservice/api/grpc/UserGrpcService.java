package com.ecmsp.userservice.api.grpc;

import com.ecmsp.user.v1.*;
import com.ecmsp.userservice.user.domain.Permission;
import com.ecmsp.userservice.user.domain.RoleFacade;
import com.ecmsp.userservice.user.domain.RoleToCreate;
import com.ecmsp.userservice.user.domain.UserFacade;
import com.ecmsp.userservice.user.domain.UserView;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(UserGrpcService.class);

    private final UserFacade userFacade;
    private final RoleFacade roleFacade;
    private final UserGrpcMapper mapper;
    private final RoleGrpcMapper roleMapper;

    public UserGrpcService(UserFacade userFacade, RoleFacade roleFacade, UserGrpcMapper mapper, RoleGrpcMapper roleMapper) {
        this.userFacade = userFacade;
        this.roleFacade = roleFacade;
        this.mapper = mapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        try {
            com.ecmsp.user.v1.UserId protoUserId = com.ecmsp.user.v1.UserId.newBuilder()
                    .setValue(request.getUserId())
                    .build();
            com.ecmsp.userservice.user.domain.UserId userId = mapper.toDomainUserId(protoUserId);
            Optional<com.ecmsp.userservice.user.domain.User> userOptional = userFacade.findUserById(userId);

            if (userOptional.isPresent()) {
                com.ecmsp.userservice.user.domain.User user = userOptional.get();
                UserView userView = new UserView(user.id(), user.login(), user.roles());
                com.ecmsp.user.v1.User protoUser = mapper.toProtoUser(userView);

                GetUserResponse response = GetUserResponse.newBuilder()
                        .setUser(protoUser)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found with id: " + request.getUserId())
                        .asRuntimeException());
            }
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        try {
            com.ecmsp.user.v1.UserToCreate protoUserToCreate = request.getUserToCreate();

            // Validate input
            if (protoUserToCreate.getLogin() == null || protoUserToCreate.getLogin().isBlank()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Login cannot be empty")
                        .asRuntimeException());
                return;
            }

            if (protoUserToCreate.getPassword() == null || protoUserToCreate.getPassword().isBlank()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Password cannot be empty")
                        .asRuntimeException());
                return;
            }

            com.ecmsp.userservice.user.domain.UserToCreate domainUserToCreate = mapper.toDomainUserToCreate(protoUserToCreate);
            com.ecmsp.userservice.user.domain.User createdUser = userFacade.createUser(domainUserToCreate);

            UserView userView = new UserView(createdUser.id(), createdUser.login(), createdUser.roles());
            com.ecmsp.user.v1.User protoUser = mapper.toProtoUser(userView);

            CreateUserResponse response = CreateUserResponse.newBuilder()
                    .setUser(protoUser)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                responseObserver.onError(Status.ALREADY_EXISTS
                        .withDescription("User with this login already exists")
                        .asRuntimeException());
            } else {
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Internal error: " + e.getMessage())
                        .asRuntimeException());
            }
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        try {
            com.ecmsp.userservice.user.domain.UserId userId = mapper.toDomainUserId(request.getUser().getId());
            String newLogin = request.getUser().getLogin();

            if (newLogin == null || newLogin.isBlank()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Login cannot be empty")
                        .asRuntimeException());
                return;
            }

            userFacade.updateUserLogin(userId, newLogin);

            // Fetch updated user
            Optional<com.ecmsp.userservice.user.domain.User> updatedUserOptional = userFacade.findUserById(userId);
            if (updatedUserOptional.isPresent()) {
                com.ecmsp.userservice.user.domain.User updatedUser = updatedUserOptional.get();
                UserView userView = new UserView(updatedUser.id(), updatedUser.login(), updatedUser.roles());
                com.ecmsp.user.v1.User protoUser = mapper.toProtoUser(userView);

                UpdateUserResponse response = UpdateUserResponse.newBuilder()
                        .setUser(protoUser)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found after update")
                        .asRuntimeException());
            }
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        try {
            com.ecmsp.user.v1.UserId protoUserId = com.ecmsp.user.v1.UserId.newBuilder()
                    .setValue(request.getUserId())
                    .build();
            com.ecmsp.userservice.user.domain.UserId userId = mapper.toDomainUserId(protoUserId);
            userFacade.deleteUser(userId);

            DeleteUserResponse response = DeleteUserResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        try {
            String filterLogin = request.getFilterLogin();
            List<UserView> users = userFacade.listUsers(filterLogin);

            List<com.ecmsp.user.v1.User> protoUsers = users.stream()
                    .map(mapper::toProtoUser)
                    .collect(Collectors.toList());

            ListUsersResponse response = ListUsersResponse.newBuilder()
                    .addAllUsers(protoUsers)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    // ==================== Role Management Methods ====================

    @Override
    public void createRole(CreateRoleRequest request, StreamObserver<CreateRoleResponse> responseObserver) {
        try {
            log.info("Creating role: {}", request.getRole().getName());

            RoleToCreate roleToCreate = roleMapper.toDomainRoleToCreate(request.getRole());
            com.ecmsp.userservice.user.domain.RoleId roleId = roleFacade.createRole(roleToCreate);

            com.ecmsp.userservice.user.domain.Role createdRole = roleFacade.findRoleById(roleId)
                    .orElseThrow(() -> new IllegalStateException("Role not found after creation"));

            CreateRoleResponse response = CreateRoleResponse.newBuilder()
                    .setRole(roleMapper.toProtoRole(createdRole))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Role created successfully: {}", roleId.value());
        } catch (IllegalArgumentException e) {
            log.error("Invalid role data: {}", e.getMessage());
            // Check if it's a duplicate role error
            if (e.getMessage().contains("already exists")) {
                responseObserver.onError(Status.ALREADY_EXISTS
                        .withDescription(e.getMessage())
                        .asRuntimeException());
            } else {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription(e.getMessage())
                        .asRuntimeException());
            }
        } catch (Exception e) {
            log.error("Error creating role", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to create role: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateRole(UpdateRoleRequest request, StreamObserver<UpdateRoleResponse> responseObserver) {
        try {
            String roleName = request.getRole().getName();
            log.info("Updating role: {}", roleName);

            // Get current role by name
            com.ecmsp.userservice.user.domain.Role currentRole = roleFacade.findRoleByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

            // Get the role ID for subsequent operations
            com.ecmsp.userservice.user.domain.RoleId roleId = currentRole.id();

            // Determine permissions to add and remove
            Set<Permission> newPermissions = request.getRole().getPermissionsList().stream()
                    .map(roleMapper::toDomainPermission)
                    .collect(Collectors.toSet());

            Set<Permission> currentPermissions = currentRole.permissions();

            // Add new permissions
            newPermissions.stream()
                    .filter(p -> !currentPermissions.contains(p))
                    .forEach(p -> roleFacade.addPermissionToRole(roleId, p));

            // Remove old permissions
            currentPermissions.stream()
                    .filter(p -> !newPermissions.contains(p))
                    .forEach(p -> roleFacade.removePermissionFromRole(roleId, p));

            // Get updated role
            com.ecmsp.userservice.user.domain.Role updatedRole = roleFacade.findRoleById(roleId)
                    .orElseThrow(() -> new IllegalStateException("Role not found after update"));

            UpdateRoleResponse response = UpdateRoleResponse.newBuilder()
                    .setRole(roleMapper.toProtoRole(updatedRole))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Role updated successfully: {}", roleName);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role data: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Error updating role", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to update role: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteRole(DeleteRoleRequest request, StreamObserver<DeleteRoleResponse> responseObserver) {
        try {
            String roleName = request.getRoleId();
            log.info("Deleting role: {}", roleName);

            roleFacade.deleteRoleByName(roleName);

            DeleteRoleResponse response = DeleteRoleResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Role deleted successfully: {}", roleName);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role name: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Error deleting role", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to delete role: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listRoles(ListRolesRequest request, StreamObserver<ListRolesResponse> responseObserver) {
        try {
            log.info("Listing all roles");

            List<com.ecmsp.userservice.user.domain.Role> roles = roleFacade.getAllRoles();
            List<com.ecmsp.user.v1.Role> protoRoles = roles.stream()
                    .map(roleMapper::toProtoRole)
                    .collect(Collectors.toList());

            ListRolesResponse response = ListRolesResponse.newBuilder()
                    .addAllRoles(protoRoles)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Listed {} roles", roles.size());
        } catch (Exception e) {
            log.error("Error listing roles", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to list roles: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void assignRoleToUsers(AssignRoleToUsersRequest request, StreamObserver<AssignRoleToUsersResponse> responseObserver) {
        try {
            String roleName = request.getRoleName();
            log.info("Assigning role {} to {} users", roleName, request.getUserIdsList().size());

            // Verify role exists
            roleFacade.findRoleByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

            // Assign role to each user
            for (String userIdString : request.getUserIdsList()) {
                com.ecmsp.userservice.user.domain.UserId userId = new com.ecmsp.userservice.user.domain.UserId(
                        java.util.UUID.fromString(userIdString)
                );
                userFacade.assignRoleToUser(userId, roleName);
            }

            AssignRoleToUsersResponse response = AssignRoleToUsersResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Role assigned successfully to {} users", request.getUserIdsList().size());
        } catch (IllegalArgumentException e) {
            log.error("Invalid assignment data: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Error assigning role to users", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to assign role: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void removeRoleFromUsers(RemoveRoleFromUsersRequest request, StreamObserver<RemoveRoleFromUsersResponse> responseObserver) {
        try {
            String roleName = request.getRoleName();
            log.info("Removing role {} from {} users", roleName, request.getUserIdsList().size());

            // Verify role exists
            roleFacade.findRoleByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

            // Remove role from each user
            for (String userIdString : request.getUserIdsList()) {
                com.ecmsp.userservice.user.domain.UserId userId = new com.ecmsp.userservice.user.domain.UserId(
                        java.util.UUID.fromString(userIdString)
                );
                userFacade.removeRoleFromUser(userId, roleName);
            }

            RemoveRoleFromUsersResponse response = RemoveRoleFromUsersResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Role removed successfully from {} users", request.getUserIdsList().size());
        } catch (IllegalArgumentException e) {
            log.error("Invalid removal data: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Error removing role from users", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to remove role: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listAllPermissions(ListAllPermissionsRequest request, StreamObserver<ListAllPermissionsResponse> responseObserver) {
        try {
            log.info("Listing all available permissions");

            List<String> permissions = Arrays.stream(Permission.values())
                    .map(Permission::name)
                    .collect(Collectors.toList());

            ListAllPermissionsResponse response = ListAllPermissionsResponse.newBuilder()
                    .addAllPermissions(permissions)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Listed {} permissions", permissions.size());
        } catch (Exception e) {
            log.error("Error listing permissions", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to list permissions: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
