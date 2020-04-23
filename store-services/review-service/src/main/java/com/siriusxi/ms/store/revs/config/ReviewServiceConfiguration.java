package com.siriusxi.ms.store.revs.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@Configuration
@Log4j2
public class ReviewServiceConfiguration {

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    Integer connectionPoolSize;

    @Bean
    public Scheduler jdbcScheduler() {
        log.info("Creates a jdbcScheduler with connectionPoolSize = {}", connectionPoolSize);
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
    }



}
