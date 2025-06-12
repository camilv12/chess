package server.handler;

import service.AuthService;
import exception.UnauthorizedException;
import model.AuthRequest;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final AuthService logoutService;

    public LogoutHandler(AuthService logoutService){
        this.logoutService = logoutService;
    }

    public Object handle(Request req, Response res){
        try{
            // Get authToken from the header
            String authToken = req.headers("authorization");
            AuthRequest request = new AuthRequest(authToken);
            logoutService.logout(request);
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(new Object());
        }catch(UnauthorizedException e){
            res.status(401);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }catch(Exception e){
            res.status(500);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }
    }
}
