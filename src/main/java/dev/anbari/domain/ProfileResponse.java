package dev.anbari.domain;

public record ProfileResponse(
        Long id,
        String username,
        String userType,
        String region,
        String tier) {
}