package com.ecmsp.userservice.user.adapter.repository.inmemory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "user.repository.type", havingValue = "in-memory")
class InMemoryUserRepositoryConfig {

    @Bean
    InMemoryUserRepository inMemoryUserRepository() {
        return new InMemoryUserRepository();
    }
}
