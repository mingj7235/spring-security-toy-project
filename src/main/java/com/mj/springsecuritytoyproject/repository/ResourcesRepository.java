package com.mj.springsecuritytoyproject.repository;

import com.mj.springsecuritytoyproject.domain.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    Resources findByResourceNameAndHttpMethod (String resourceName, String httpMethod);

    /**
     * QueryDSL 로 변경
     */

    @Query ("select r from Resources r join fetch r.roleSet where r.resourceType = 'url' order by r.orderNum desc ")
    List<Resources> findAllResources();

    @Query ("select r from Resources r join fetch r.roleSet where r.resourceType = 'method' order by r.orderNum desc")
    List<Resources> findAllMethodResources();

    @Query ("select r from Resources r join fetch r.roleSet where r.resourceType = 'pointcut' order by r.orderNum desc")
    List<Resources> findAllPointcutResources();

}
