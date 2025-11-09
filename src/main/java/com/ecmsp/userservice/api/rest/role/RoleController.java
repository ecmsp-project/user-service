package com.ecmsp.userservice.api.rest.role;

import com.ecmsp.userservice.user.domain.Role;
import com.ecmsp.userservice.user.domain.RoleFacade;
import com.ecmsp.userservice.user.domain.RoleId;
import com.ecmsp.userservice.user.domain.RoleToCreate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleFacade roleFacade;

    public RoleController(RoleFacade roleFacade) {
        this.roleFacade = roleFacade;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        if (e.getMessage().contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    record ErrorResponse(String message) {
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleCreateRequest request) {
        RoleToCreate roleToCreate = new RoleToCreate(request.name(), request.permissions());
        RoleId roleId = roleFacade.createRole(roleToCreate);

        Role createdRole = roleFacade.findRoleById(roleId)
                .orElseThrow(() -> new IllegalStateException("Role was not created"));

        RoleResponse response = new RoleResponse(
                createdRole.id().value(),
                createdRole.name(),
                createdRole.permissions()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<RoleResponse> getRoleByName(@PathVariable String roleName) {
        return roleFacade.findRoleByName(roleName)
                .map(role -> new RoleResponse(
                        role.id().value(),
                        role.name(),
                        role.permissions()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleFacade.getAllRoles().stream()
                .map(role -> new RoleResponse(
                        role.id().value(),
                        role.name(),
                        role.permissions()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(roles);
    }

    @PostMapping("/{roleName}/permissions")
    public ResponseEntity<Void> addPermissionToRole(
            @PathVariable String roleName,
            @RequestBody AddPermissionRequest request) {
        roleFacade.addPermissionToRoleByName(roleName, request.permission());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleName}/permissions/{permission}")
    public ResponseEntity<Void> removePermissionFromRole(
            @PathVariable String roleName,
            @PathVariable String permission) {
        roleFacade.removePermissionFromRoleByName(
                roleName,
                com.ecmsp.userservice.user.domain.Permission.valueOf(permission)
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleName}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleName) {
        roleFacade.deleteRoleByName(roleName);
        return ResponseEntity.noContent().build();
    }
}
