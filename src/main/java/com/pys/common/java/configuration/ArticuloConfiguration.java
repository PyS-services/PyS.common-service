package com.pys.common.java.configuration;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableDiscoveryClient
@EnableJpaAuditing
public class ArticuloConfiguration {
}