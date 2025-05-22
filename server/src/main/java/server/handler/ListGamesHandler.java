package server.handler;

import service.ListGamesService;

public class ListGamesHandler {
    private final ListGamesService listGamesService;

    public ListGamesHandler(ListGamesService listGamesService){
        this.listGamesService = listGamesService;
    }
}
