package com.mj.springsecuritytoyproject.controller.admin;

import com.mj.springsecuritytoyproject.domain.RoleHierarchy;
import com.mj.springsecuritytoyproject.domain.dto.RoleHierarchyDto;
import com.mj.springsecuritytoyproject.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoleHierarchyController {

    private final RoleHierarchyService roleHierarchyService;

    @GetMapping ("/admin/hierarchy")
    public String getHierarchy(Model model) {

        List<RoleHierarchyDto> roleHierarchies = roleHierarchyService.getRoleHierarchies().stream()
                .map(r -> {
                    RoleHierarchy childRole = roleHierarchyService.findByChildName(r.getChildName());
                    String parentRoleNameByChildName = roleHierarchyService.findParentRoleNameByChildName(r.getChildName());
                    return RoleHierarchyDto.toDto(r.getId(), childRole.getChildName(), parentRoleNameByChildName);
                })
                .collect(Collectors.toList());

        model.addAttribute("roleHierarchies", roleHierarchies);

        return "admin/hierarchy/list";
    }

    @GetMapping ("/admin/hierarchy/register")
    public String register(Model model) {

        return "admin/hierarchy/detail";
    }

}
