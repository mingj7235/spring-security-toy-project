package com.mj.springsecuritytoyproject.repository;

import com.mj.springsecuritytoyproject.domain.AccessIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessIpRepository extends JpaRepository<AccessIp, Long> {

    AccessIp findByIpAddress(String ipAddress);

}
