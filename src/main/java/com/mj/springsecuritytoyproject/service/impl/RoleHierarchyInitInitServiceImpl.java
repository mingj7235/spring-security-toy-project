package com.mj.springsecuritytoyproject.service.impl;

import com.mj.springsecuritytoyproject.domain.RoleHierarchy;
import com.mj.springsecuritytoyproject.repository.RoleHierarchyRepository;
import com.mj.springsecuritytoyproject.service.RoleHierarchyInitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleHierarchyInitInitServiceImpl implements RoleHierarchyInitService {

    private final RoleHierarchyRepository roleHierarchyRepository;

    @Override
    public String findAllHierarchy() {

        List<RoleHierarchy> rolesHierarchy = roleHierarchyRepository.findAll();

        Iterator<RoleHierarchy> iterator = rolesHierarchy.iterator();

        /**
         * Role Hierarchy Formatting
         */

        StringBuffer concatedRoles = new StringBuffer();
        while (iterator.hasNext()) {
            RoleHierarchy model = iterator.next();
            if (model.getParentName() != null) {
                concatedRoles.append(model.getParentName().getChildName());
                concatedRoles.append(" > ");
                concatedRoles.append(model.getChildName());
                concatedRoles.append("\n");
            }
        }

        return concatedRoles.toString();
    }
}
