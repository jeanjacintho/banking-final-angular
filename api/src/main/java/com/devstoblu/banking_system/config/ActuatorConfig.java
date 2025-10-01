package com.devstoblu.banking_system.config;

import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ActuatorConfig {

    @Bean
    @Profile("!docker")
    public MeterRegistryCustomizer<?> meterRegistryCustomizer() {
        return registry -> {
            try {
                new UptimeMetrics().bindTo(registry);
            } catch (Exception e) {
                System.err.println("Warning: Could not bind system metrics: " + e.getMessage());
            }
        };
    }
}
