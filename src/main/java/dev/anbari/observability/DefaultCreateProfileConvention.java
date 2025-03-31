package dev.anbari.observability;

import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.Nullable;

public class DefaultCreateProfileConvention implements CreateProfileConvention{
    @Override
    public @Nullable String getName() {
        return "profile.create";
    }

    @Override
    public KeyValues getLowCardinalityKeyValues(CreateProfileContext context) {
        return KeyValues.of(
                "user.type", context.getUserType(),
                "region", context.getRegion(),
                "tier", context.getTier()
        );
    }
}
