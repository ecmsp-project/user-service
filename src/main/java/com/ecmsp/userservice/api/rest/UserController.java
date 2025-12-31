package com.ecmsp.userservice.api.rest;


import com.ecmsp.userservice.api.rest.role.AssignRoleRequest;
import com.ecmsp.userservice.api.rest.role.RoleResponse;
import com.ecmsp.userservice.user.domain.Role;
import com.ecmsp.userservice.user.domain.UserFacade;
import com.ecmsp.userservice.user.domain.UserId;
import com.ecmsp.userservice.user.domain.UserToCreate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest request) {
        UserToCreate user = new UserToCreate(request.login(), request.password());
        userFacade.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<Void> assignRoleToUser(
            @PathVariable UUID userId,
            @RequestBody AssignRoleRequest request) {
        userFacade.assignRoleToUser(new UserId(userId), request.roleName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable UUID userId,
            @PathVariable String roleName) {
        userFacade.removeRoleFromUser(new UserId(userId), roleName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleResponse>> getUserRoles(@PathVariable UUID userId) {
        Set<Role> roles = userFacade.getUserRoles(new UserId(userId));

        List<RoleResponse> roleResponses = roles.stream()
                .map(role -> new RoleResponse(
                        role.id().value(),
                        role.name(),
                        role.permissions()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(roleResponses);
    }
}
