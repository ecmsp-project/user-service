package com.ecmsp.userservice.user.config;

import com.ecmsp.userservice.user.adapter.repository.db.RoleEntityRepository;
import com.ecmsp.userservice.user.adapter.repository.db.UserEntityRepository;
import com.ecmsp.userservice.user.domain.RoleRepository;
import com.ecmsp.userservice.user.domain.UserFacade;
import com.ecmsp.userservice.user.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserConfig {
    @Bean
    public UserFacade userFacade(UserRepository userRepository, RoleEntityRepository roleEntityRepository, UserEntityRepository userEntityRepository){
        return new UserFacade(userRepository, roleEntityRepository, userEntityRepository);
    }
}
