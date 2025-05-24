package server.handler;

import service.AuthService;
import service.BadRequestException;
import service.CreateGameService;
import service.UnauthorizedException;
import service.model.AuthRequest;
import service.model.CreateGameRequest;
import service.model.CreateGameResult;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final AuthService authService;
    private final CreateGameService createGameService;

    public CreateGameHandler(AuthService authService, CreateGameService createGameService){
        this.authService = authService;
        this.createGameService = createGameService;
    }

    public Object handle(Request req, Response res){
        try{
            String authToken = req.headers("authorization");
            authService.authenticate(new AuthRequest(authToken));
            CreateGameRequest request = JsonUtils.fromJson(req, CreateGameRequest.class);
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
