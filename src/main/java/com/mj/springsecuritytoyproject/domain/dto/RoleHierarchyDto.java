package com.mj.springsecuritytoyproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleHierarchyDto {

    private Long id;

    private String roleName;

    private String parentRoleName;

    public static RoleHierarchyDto toDto (final Long id, final String childRole, final String parentRoleName) {
        return RoleHierarchyDto.builder()
                .id(id)
                .roleName(childRole)
                .parentRoleName(parentRoleName)
                .build();
    }

}
