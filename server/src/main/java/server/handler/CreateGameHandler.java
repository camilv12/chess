package server.handler;

import service.CreateGameService;

public class CreateGameHandler {
    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService){
        this.createGameService = createGameService;
    }
}
