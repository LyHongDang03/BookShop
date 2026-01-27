package lyhongdang.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.entity.Permission;
import lyhongdang.book.entity.Role;
import lyhongdang.book.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permissions")
@Tag(name = "Permissions API")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Create a new permission", description = "Add a new permission to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permission created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Permission> create(@RequestBody Permission permission) {
        Permission created = permissionService.createPermission(permission);
        return ResponseEntity.created(URI.create("/permissions/" + created.getId())).body(created);
    }

    @Operation(summary = "Get all permissions", description = "Retrieve the list of all available permissions")
    @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully")
    @GetMapping
    public List<Permission> list() {
        return permissionService.getAll();
    }

    @Operation(summary = "Get permission by ID", description = "Retrieve a specific permission by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @GetMapping("/{id}")
    public Permission get(@PathVariable Integer id) {
        return permissionService.getById(id);
    }

    @Operation(summary = "Delete permission", description = "Delete a permission by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permission deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        permissionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign permissions to a role", description = "Assign one or more permissions to a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissions assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Role or permissions not found", content = @Content)
    })
    @PostMapping("/assign")
    public Role assign(@RequestParam Integer roleId, @RequestBody IdsRequest request) {
        return permissionService.assignPermissionsToRole(roleId, request.getIds());
    }

    @Operation(summary = "Revoke permissions from a role", description = "Remove one or more permissions from a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissions revoked successfully"),
            @ApiResponse(responseCode = "404", description = "Role or permissions not found", content = @Content)
    })
    @PostMapping("/revoke")
    public Role revoke(@RequestParam Integer roleId, @RequestBody IdsRequest request) {
        return permissionService.revokePermissionsFromRole(roleId, request.getIds());
    }

    @Data
    public static class IdsRequest {
        private List<Integer> ids;
    }
}
