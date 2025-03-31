package dev.anbari.controller;

import dev.anbari.domain.ProfileRequest;
import dev.anbari.domain.ProfileResponse;
import dev.anbari.service.ProfileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ProfileResponse create(@RequestBody ProfileRequest request) {
        return profileService.create(request);
    }
}
