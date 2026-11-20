package com.fitsync.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/** "Hoy" se calcula en la zona horaria del usuario (Lima), aunque el servidor esté en UTC. */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock(@Value("${fitsync.zona-horaria:America/Lima}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}
