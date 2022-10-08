package com.mj.springsecuritytoyproject.config;

import com.mj.springsecuritytoyproject.repository.ResourcesRepository;
import com.mj.springsecuritytoyproject.service.SecurityResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AppConfig {

    @Bean
    public SecurityResourceService securityResourceService(ResourcesRepository resourcesRepository){
        return new SecurityResourceService(resourcesRepository);
    }

}
