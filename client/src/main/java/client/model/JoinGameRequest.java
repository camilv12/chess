package client.model;

public record JoinGameRequest(
    String authToken,
    String playerColor,
    Integer gameID
) {}
