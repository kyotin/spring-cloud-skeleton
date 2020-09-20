package com.ge.takt.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableAuthorizationServer
@EnableResourceServer
@EnableFeignClients(basePackages = { "com.ge.takt.uaa.feignClient" })
@ComponentScan(basePackages = {"com.ge.takt.uaa", "com.ge.takt.common"})
@EntityScan(basePackages = {"com.ge.takt.uaa", "com.ge.takt.common"})
@EnableJpaRepositories(basePackages = {"com.ge.takt.uaa", "com.ge.takt.common"})
@EnableDiscoveryClient
public class UaaApplication {
    public static void main(String[] args) {
        SpringApplication.run(UaaApplication.class, args);
    }
}

