package dev.anbari.observability;

import dev.anbari.domain.ProfileRequest;
import dev.anbari.domain.ProfileResponse;
import dev.anbari.service.ProfileService;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObservedProfileService implements ProfileService {

    // Defines default span name & metric tags (region, user.type, tier)
    private static final CreateProfileConvention DEFAULT_CONVENTION = new DefaultCreateProfileConvention();

    // The actual service performing business logic
    private final ProfileService delegate;
    private final ObservationRegistry registry;

    // For custom business metrics (e.g., counters, timers)
    private final MeterRegistry meterRegistry;

    // Optional override for observation convention
    @Nullable
    private final CreateProfileConvention customConvention;

    @Override
    public ProfileResponse create(ProfileRequest request) {

        // Custom Business Counter (counts only EU region profiles)
        // Metric Name: profile.created.eu
        // Exported ONLY to Prometheus
        if ("EU".equalsIgnoreCase(request.region())) {
            meterRegistry.counter("profile.created.eu").increment();
        }

        // Observation (produces span + timer automatically)
        return Observation.createNotStarted(
                        customConvention,              // Optional
                        DEFAULT_CONVENTION,            // Fallback convention (sets name + tags)
                        () -> new CreateProfileContext(
                                request.userType(),    // Will become both span attribute and metric tag
                                request.region(),
                                request.tier()
                        ),
                        registry                       // Links this observation to OTEL
                )
                // ✅ Executes the actual business logic & captures tracing + timing
                .observe(() -> delegate.create(request));
    }
}
