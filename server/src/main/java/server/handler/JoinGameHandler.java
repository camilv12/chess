package server.handler;

import service.JoinGameService;

public class JoinGameHandler {
    private final JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService joinGameService){
        this.joinGameService = joinGameService;
    }
}
