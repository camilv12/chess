package server.handler;

import exception.AlreadyTakenException;
import exception.BadRequestException;
import service.GameSessionService;
import exception.UnauthorizedException;
import model.JoinGameRequest;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final GameSessionService gameSessionService;

    public JoinGameHandler(GameSessionService gameSessionService){
        this.gameSessionService = gameSessionService;
    }

    public Object handle(Request req, Response res){
        try{
            String authToken = req.headers("authorization");
            JoinGameRequest joinBody = JsonUtils.fromJson(req, JoinGameRequest.class);
            JoinGameRequest request = new JoinGameRequest(authToken, joinBody.playerColor(), joinBody.gameID());
            gameSessionService.joinGame(request);
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(new Object());
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
