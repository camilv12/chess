package server.handler;

import service.AuthService;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import model.AuthRequest;
import model.CreateGameRequest;
import model.CreateGameResult;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final AuthService authService;
    private final GameService gameService;

    public CreateGameHandler(AuthService authService, GameService gameService){
        this.authService = authService;
        this.gameService = gameService;
    }

    public Object handle(Request req, Response res){
        try{
            String authToken = req.headers("authorization");
            authService.authenticate(new AuthRequest(authToken));
            CreateGameRequest request = JsonUtils.fromJson(req, CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(request);
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
