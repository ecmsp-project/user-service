package com.ecmsp.userservice.application.config;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
@Profile("local")
public class LocalPostgreSQLConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("user-service-db")
                .withCreateContainerCmdModifier(cmd ->
                        cmd
                                .withName("postgres-user-db")
                                .getHostConfig().withPortBindings(
                                        new PortBinding(
                                                /* hostPort = */ Ports.Binding.bindPort(5432),
                                                /* containerPort = */ new ExposedPort(5432)
                                        )
                                )
                )
                .withUsername("admin")
                .withPassword("admin");
    }

}