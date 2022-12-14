package com.mj.springsecuritytoyproject.service;

import com.mj.springsecuritytoyproject.domain.Role;

import java.util.List;

public interface RoleService {

    Role getRole(long id);

    Role findByRoleName (String name);

    List<Role> getRoles();

    void createRole(Role role);

    void deleteRole(long id);
}
