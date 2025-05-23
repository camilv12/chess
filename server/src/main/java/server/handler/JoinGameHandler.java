package server.handler;

import service.AlreadyTakenException;
import service.BadRequestException;
import service.JoinGameService;
import service.UnauthorizedException;
import service.model.JoinGameRequest;
import service.model.JoinGameResult;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService joinGameService){
        this.joinGameService = joinGameService;
    }

    public Object handle(Request req, Response res){
        try{
            String authToken = req.headers("authorization");
            JoinGameRequest joinBody = JsonUtils.fromJson(req, JoinGameRequest.class);
            JoinGameRequest request = new JoinGameRequest(authToken, joinBody.playerColor(), joinBody.gameID());
            JoinGameResult result = joinGameService.joinGame(request);
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(result);
        }catch(BadRequestException e){
            res.status(400);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }catch(UnauthorizedException e){
            res.status(401);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }catch(AlreadyTakenException e){
            res.status(403);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }catch(Exception e){
            res.status(500);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }
    }
}
