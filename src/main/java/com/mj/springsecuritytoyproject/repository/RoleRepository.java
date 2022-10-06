package com.mj.springsecuritytoyproject.repository;

import com.mj.springsecuritytoyproject.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String name);

    @Override
    void delete(Role role);
}
