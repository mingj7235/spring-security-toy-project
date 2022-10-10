package com.mj.springsecuritytoyproject.service;

import com.mj.springsecuritytoyproject.domain.Role;
import com.mj.springsecuritytoyproject.domain.RoleHierarchy;

import java.util.List;

public interface RoleHierarchyService {

    List<RoleHierarchy> getRoleHierarchies();

    RoleHierarchy findByChildName(String childName);

    String findParentRoleNameByChildName(String childName);

    void createRoleHierarchy(Role childRole, Role parentRole);

}
