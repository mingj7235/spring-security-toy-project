package com.mj.springsecuritytoyproject.service.impl;

import com.mj.springsecuritytoyproject.domain.Role;
import com.mj.springsecuritytoyproject.domain.RoleHierarchy;
import com.mj.springsecuritytoyproject.repository.RoleHierarchyRepository;
import com.mj.springsecuritytoyproject.security.init.SecurityInitializer;
import com.mj.springsecuritytoyproject.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

    private final RoleHierarchyRepository roleHierarchyRepository;

    private final SecurityInitializer securityInitializer;

    private final ApplicationArguments argsConstructor;

    @Override
    public RoleHierarchy findByChildName(final String childName) {
       return roleHierarchyRepository.findByChildName(childName);
    }

    @Override
    public String findParentRoleNameByChildName(final String childName) {
        RoleHierarchy childRole = roleHierarchyRepository.findByChildName(childName);

        RoleHierarchy parentRole = childRole.getParentName();

        if (parentRole == null) {
            return "부모 없음";
        }

        return parentRole.getChildName();
    }

    @Override
    public List<RoleHierarchy> getRoleHierarchies() {
        return roleHierarchyRepository.findAll();
    }

    @Override
    public void createRoleHierarchy(final Role childRole, final Role parentRole) {
        RoleHierarchy roleHierarchy = roleHierarchyRepository.findByChildName(parentRole.getRoleName());

        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .childName(parentRole.getRoleName())
                    .build();
        }

        RoleHierarchy parentRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);

        roleHierarchy = roleHierarchyRepository.findByChildName(childRole.getRoleName());

        if (roleHierarchy == null) {
            roleHierarchy =RoleHierarchy.builder()
                    .childName(childRole.getRoleName())
                    .build();
        }

        RoleHierarchy childRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);
        childRoleHierarchy.setParentName(parentRoleHierarchy);

        securityInitializer.run(argsConstructor);
    }

}
