package com.mj.springsecuritytoyproject.security.init;

import com.mj.springsecuritytoyproject.service.RoleHierarchyInitService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

/**
 * Server 기동시, Spring Security 가 초기화 될 때, RoleHierarchy 를 매핑하여 계층구조를 설정한다.
 */

@Component
@RequiredArgsConstructor
public class SecurityInitializer implements ApplicationRunner {

    private final RoleHierarchyInitService roleHierarchyInitService;

    private final RoleHierarchyImpl roleHierarchy;

    @Override
    public void run(final ApplicationArguments args) {
        String allHierarchy = roleHierarchyInitService.findAllHierarchy();
        roleHierarchy.setHierarchy(allHierarchy);
    }

}
