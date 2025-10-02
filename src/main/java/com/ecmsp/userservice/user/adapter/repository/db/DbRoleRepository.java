package com.ecmsp.userservice.user.adapter.repository.db;

import com.ecmsp.userservice.user.domain.Role;
import com.ecmsp.userservice.user.domain.RoleId;
import com.ecmsp.userservice.user.domain.RoleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
class DbRoleRepository implements RoleRepository {

    private final RoleEntityRepository roleEntityRepository;
    private final RoleEntityMapper roleEntityMapper;

    public DbRoleRepository(RoleEntityRepository roleEntityRepository) {
        this.roleEntityRepository = roleEntityRepository;
        this.roleEntityMapper = new RoleEntityMapper();
    }

    @Override
    public void save(Role role) {
        RoleEntity entity = roleEntityMapper.toRoleEntity(role);
        roleEntityRepository.save(entity);
    }

    @Override
    public Optional<Role> findById(RoleId roleId) {
        return roleEntityRepository.findById(roleId.value())
                .map(roleEntityMapper::toRole);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleEntityRepository.findByRoleName(name)
                .map(roleEntityMapper::toRole);
    }

    @Override
    public List<Role> findAll() {
        return roleEntityRepository.findAll().stream()
                .map(roleEntityMapper::toRole)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(RoleId roleId) {
        roleEntityRepository.deleteById(roleId.value());
    }
}
