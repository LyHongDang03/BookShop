package lyhongdang.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.entity.Permission;
import lyhongdang.book.entity.Role;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.PermissionRepository;
import lyhongdang.book.repository.RoleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @PreAuthorize("hasAnyRole('ADMIN')")
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public Permission getById(Integer id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCodes.PERMISSION_NOT_FOUND));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteById(Integer id) {
        permissionRepository.deleteById(id);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Role assignPermissionsToRole(Integer roleId, List<Integer> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->  new BusinessException(ErrorCodes.ROLE_NOT_FOUND));

        Set<Permission> toAssign = new HashSet<>(permissionRepository.findAllById(permissionIds));
        if (toAssign.size() != new HashSet<>(permissionIds).size()) {
            throw new BusinessException(ErrorCodes.ROLE_NOT_FOUND);
        }

        role.getPermissions().addAll(toAssign);
        return roleRepository.save(role);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Role revokePermissionsFromRole(Integer roleId, List<Integer> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ROLE_NOT_FOUND));

        Set<Permission> toRevoke = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.getPermissions().removeAll(toRevoke);
        return roleRepository.save(role);
    }
}
