package dev.anbari.config;

import dev.anbari.observability.ObservedProfileService;
import dev.anbari.service.ProfileService;
import dev.anbari.service.ProfileServiceImp;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ProfileService profileService(
            ProfileServiceImp delegate,
            ObservationRegistry registry,
            MeterRegistry meterRegistry // ✅ new argument
    ) {
        return new ObservedProfileService(delegate, registry, meterRegistry, null);
    }
}
