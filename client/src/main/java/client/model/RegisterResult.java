package client.model;

public record RegisterResult(
        String username,
        String authToken
) {}
