package dev.anbari.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

public interface CreateProfileConvention extends ObservationConvention<CreateProfileContext> {
    @Override
    default boolean supportsContext(Observation.Context context) {
        return context instanceof CreateProfileContext;
    }
}
