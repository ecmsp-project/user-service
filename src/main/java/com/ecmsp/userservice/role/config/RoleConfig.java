package com.ecmsp.userservice.role.config;

import com.ecmsp.userservice.user.domain.RoleFacade;
import com.ecmsp.userservice.user.domain.RoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RoleConfig {
    @Bean
    public RoleFacade roleFacade(RoleRepository roleRepository) {
        return new RoleFacade(roleRepository);
    }
}
