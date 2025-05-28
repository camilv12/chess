package server.handler;

import service.*;
import service.model.AuthRequest;
import service.model.ListGamesResult;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    private final AuthService authService;
    private final GameService gameService;

    public ListGamesHandler(AuthService authService, GameService gamesService){
        this.authService = authService;
        this.gameService = gamesService;
    }

    public Object handle(Request req, Response res){
        try{
            String authToken = req.headers("authorization");
            authService.authenticate(new AuthRequest(authToken));

            ListGamesResult result = gameService.listGames();
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(result);
        }catch(UnauthorizedException e){
            res.status(401);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }catch(Exception e){
            res.status(500);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }
    }
}
