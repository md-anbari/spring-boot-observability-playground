package dev.anbari.service;

import dev.anbari.domain.ProfileRequest;
import dev.anbari.domain.ProfileResponse;

public interface ProfileService {
    ProfileResponse create(ProfileRequest request);
}
