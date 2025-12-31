package com.ecmsp.userservice.user.domain;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    void save(Role role);
    Optional<Role> findById(RoleId roleId);
    Optional<Role> findByName(String name);
    List<Role> findAll();
    void delete(RoleId roleId);
}
