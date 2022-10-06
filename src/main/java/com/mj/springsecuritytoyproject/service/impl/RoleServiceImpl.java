package com.mj.springsecuritytoyproject.service.impl;

import com.mj.springsecuritytoyproject.domain.Role;
import com.mj.springsecuritytoyproject.repository.RoleRepository;
import com.mj.springsecuritytoyproject.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional (readOnly = true)
    public Role getRole(long id) {
        return roleRepository.findById(id).orElse(new Role());
    }

    @Override
    @Transactional (readOnly = true)
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void createRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void deleteRole(long id) {
        roleRepository.deleteById(id);
    }
}
