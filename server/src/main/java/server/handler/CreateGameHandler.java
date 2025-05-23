package server.handler;

import service.BadRequestException;
import service.CreateGameService;
import service.UnauthorizedException;
import service.model.CreateGameRequest;
import service.model.CreateGameResult;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService){
        this.createGameService = createGameService;
    }

    public Object handle(Request req, Response res){
        try{
            String authToken = req.headers("authorization");
            String gameName = JsonUtils.fromJson(req, CreateGameRequest.class).gameName();
            CreateGameRequest request = new CreateGameRequest(authToken, gameName);
            CreateGameResult result = createGameService.createGame(request);
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(result);
        } catch (BadRequestException e){
            res.status(400);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        } catch (UnauthorizedException e){
            res.status(401);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        } catch (Exception e) {
            res.status(500);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }
    }
}
