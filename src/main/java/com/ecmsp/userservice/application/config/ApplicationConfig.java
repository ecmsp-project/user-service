package com.ecmsp.userservice.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ApplicationConfig {
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }


}
