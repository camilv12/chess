package server.handler;

import service.AuthService;
import service.BadRequestException;
import service.UnauthorizedException;
import service.model.LoginRequest;
import service.model.LoginResult;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final AuthService authService;

    public LoginHandler(AuthService authService){
        this.authService = authService;
    }

    public Object handle(Request req, Response res){
        try{
            LoginRequest request = JsonUtils.fromJson(req, LoginRequest.class);
            LoginResult result = authService.login(request);
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(result);
        }catch(BadRequestException e){
            res.status(400);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }catch(UnauthorizedException e){
            res.status(401);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }catch(Exception e){
            res.status(500);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }
    }
}
