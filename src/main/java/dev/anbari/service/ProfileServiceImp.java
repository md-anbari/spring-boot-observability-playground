package dev.anbari.service;

import dev.anbari.domain.ProfileEntity;
import dev.anbari.domain.ProfileRequest;
import dev.anbari.domain.ProfileResponse;
import dev.anbari.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImp implements ProfileService{
    private final ProfileRepository repository;

    public ProfileServiceImp(ProfileRepository repository) {
        this.repository = repository;
    }
    @Override
    public ProfileResponse create(ProfileRequest request) {
        ProfileEntity entity = ProfileEntity.builder()
                .username(request.username())
                .userType(request.userType())
                .region(request.region())
                .tier(request.tier())
                .build();

        ProfileEntity saved = repository.save(entity);

        return new ProfileResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getUserType(),
                saved.getRegion(),
                saved.getTier()
        );
    }
}
