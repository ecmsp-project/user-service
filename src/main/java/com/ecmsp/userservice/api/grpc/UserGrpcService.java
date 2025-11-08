package com.ecmsp.userservice.api.grpc;

import com.ecmsp.user.v1.*;
import com.ecmsp.userservice.user.domain.User;
import com.ecmsp.userservice.user.domain.UserFacade;
import com.ecmsp.userservice.user.domain.UserId;
import com.ecmsp.userservice.user.domain.UserView;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    private final UserFacade userFacade;
    private final UserGrpcMapper mapper;

    public UserGrpcService(UserFacade userFacade, UserGrpcMapper mapper) {
        this.userFacade = userFacade;
        this.mapper = mapper;
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        try {
            com.ecmsp.user.v1.UserId protoUserId = com.ecmsp.user.v1.UserId.newBuilder()
                    .setValue(request.getUserId())
                    .build();
            UserId userId = mapper.toDomainUserId(protoUserId);
            Optional<User> userOptional = userFacade.findUserById(userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
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
            User createdUser = userFacade.createUser(domainUserToCreate);

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
            UserId userId = mapper.toDomainUserId(request.getUser().getId());
            String newLogin = request.getUser().getLogin();

            if (newLogin == null || newLogin.isBlank()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Login cannot be empty")
                        .asRuntimeException());
                return;
            }

            userFacade.updateUserLogin(userId, newLogin);

            // Fetch updated user
            Optional<User> updatedUserOptional = userFacade.findUserById(userId);
            if (updatedUserOptional.isPresent()) {
                User updatedUser = updatedUserOptional.get();
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
            UserId userId = mapper.toDomainUserId(protoUserId);
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

    @Override
    public void createRole(CreateRoleRequest request, StreamObserver<CreateRoleResponse> responseObserver) {
        // TODO: Implement role management later
        responseObserver.onError(Status.UNIMPLEMENTED
                .withDescription("Role management not yet implemented")
                .asRuntimeException());
    }

    @Override
    public void updateRole(UpdateRoleRequest request, StreamObserver<UpdateRoleResponse> responseObserver) {
        // TODO: Implement role management later
        responseObserver.onError(Status.UNIMPLEMENTED
                .withDescription("Role management not yet implemented")
                .asRuntimeException());
    }

    @Override
    public void deleteRole(DeleteRoleRequest request, StreamObserver<DeleteRoleResponse> responseObserver) {
        // TODO: Implement role management later
        responseObserver.onError(Status.UNIMPLEMENTED
                .withDescription("Role management not yet implemented")
                .asRuntimeException());
    }

    @Override
    public void listRoles(ListRolesRequest request, StreamObserver<ListRolesResponse> responseObserver) {
        // TODO: Implement role management later
        responseObserver.onError(Status.UNIMPLEMENTED
                .withDescription("Role management not yet implemented")
                .asRuntimeException());
    }

    @Override
    public void assignRoleToUsers(AssignRoleToUsersRequest request, StreamObserver<AssignRoleToUsersResponse> responseObserver) {
        // TODO: Implement role management later
        responseObserver.onError(Status.UNIMPLEMENTED
                .withDescription("Role management not yet implemented")
                .asRuntimeException());
    }

    @Override
    public void removeRoleFromUsers(RemoveRoleFromUsersRequest request, StreamObserver<RemoveRoleFromUsersResponse> responseObserver) {
        // TODO: Implement role management later
        responseObserver.onError(Status.UNIMPLEMENTED
                .withDescription("Role management not yet implemented")
                .asRuntimeException());
    }

    @Override
    public void listAllPermissions(ListAllPermissionsRequest request, StreamObserver<ListAllPermissionsResponse> responseObserver) {
        // TODO: Implement role management later
        responseObserver.onError(Status.UNIMPLEMENTED
                .withDescription("Role management not yet implemented")
                .asRuntimeException());
    }
}
