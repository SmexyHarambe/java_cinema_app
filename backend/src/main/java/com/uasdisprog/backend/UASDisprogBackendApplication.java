package com.uasdisprog.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UASDisprogBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UASDisprogBackendApplication.class, args);
    }
}
