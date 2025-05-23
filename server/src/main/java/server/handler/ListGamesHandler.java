package server.handler;

import service.*;
import service.model.ListGamesRequest;
import service.model.ListGamesResult;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    private final ListGamesService listGamesService;

    public ListGamesHandler(ListGamesService listGamesService){
        this.listGamesService = listGamesService;
    }

    public Object handle(Request req, Response res){
        try{
            String authToken = req.headers("authorization");
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResult result = listGamesService.listGames(request);
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
