package com.github.sawied.microservice.trade.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * code from oneport chinasoft team.
 */
@Configuration
@EntityScan(basePackages = {"com.github.sawied.microservice.trade.jpa.entity"})
@EnableJpaRepositories(basePackages = {"com.github.sawied.microservice.trade.jpa.repository"})
public class JPAConfig {
}
