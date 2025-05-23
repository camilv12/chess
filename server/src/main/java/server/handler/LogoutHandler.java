package server.handler;

import service.LogoutService;
import service.UnauthorizedException;
import service.model.LogoutRequest;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService){
        this.logoutService = logoutService;
    }

    public Object handle(Request req, Response res){
        try{
            // Get authToken from the header
            String authToken = req.headers("authorization");
            LogoutRequest request = new LogoutRequest(authToken);
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
