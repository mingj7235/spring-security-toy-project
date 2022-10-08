package com.mj.springsecuritytoyproject.repository;

import com.mj.springsecuritytoyproject.domain.RoleHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {

    RoleHierarchy findByChildName(String roleName);

}
