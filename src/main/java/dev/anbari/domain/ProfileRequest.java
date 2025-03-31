package dev.anbari.domain;

public record ProfileRequest(
        String username,
        String userType,
        String region,
        String tier) {
}