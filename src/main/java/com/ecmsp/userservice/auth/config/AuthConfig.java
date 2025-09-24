package com.ecmsp.userservice.auth.config;

import com.ecmsp.userservice.auth.domain.AuthFacade;
import com.ecmsp.userservice.auth.domain.TokenGenerator;
import com.ecmsp.userservice.user.domain.UserFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AuthConfig {
    @Bean
    public AuthFacade authFacade(UserFacade userFacade, TokenGenerator generator){
        return new AuthFacade(userFacade, generator);
    }
}
