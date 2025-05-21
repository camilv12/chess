package service.model;

public record LoginResult(
        String username,
        String authToken
) {}
