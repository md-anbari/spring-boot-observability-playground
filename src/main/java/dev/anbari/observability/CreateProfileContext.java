package dev.anbari.observability;

import io.micrometer.observation.Observation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateProfileContext extends Observation.Context {

    private final String userType;
    private final String region;
    private final String tier;
}
