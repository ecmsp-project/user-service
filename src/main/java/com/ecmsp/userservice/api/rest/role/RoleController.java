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

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID roleId) {
        return roleFacade.findRoleById(new RoleId(roleId))
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

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<Void> addPermissionToRole(
            @PathVariable UUID roleId,
            @RequestBody AddPermissionRequest request) {
        roleFacade.addPermissionToRole(new RoleId(roleId), request.permission());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}/permissions/{permission}")
    public ResponseEntity<Void> removePermissionFromRole(
            @PathVariable UUID roleId,
            @PathVariable String permission) {
        roleFacade.removePermissionFromRole(
                new RoleId(roleId),
                com.ecmsp.userservice.user.domain.Permission.valueOf(permission)
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID roleId) {
        roleFacade.deleteRole(new RoleId(roleId));
        return ResponseEntity.noContent().build();
    }
}
