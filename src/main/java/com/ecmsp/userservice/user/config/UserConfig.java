package com.ecmsp.userservice.user.config;

import com.ecmsp.userservice.user.domain.RoleRepository;
import com.ecmsp.userservice.user.domain.UserFacade;
import com.ecmsp.userservice.user.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserConfig {
    @Bean
    public UserFacade userFacade(UserRepository userRepository, RoleRepository roleRepository){
        return new UserFacade(userRepository, roleRepository);
    }
}
