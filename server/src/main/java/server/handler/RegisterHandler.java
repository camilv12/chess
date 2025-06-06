package server.handler;
import service.AlreadyTakenException;
import service.AuthService;
import service.BadRequestException;
import model.RegisterRequest;
import model.RegisterResult;
import spark.Request;

import spark.Response;

public class RegisterHandler {
    private final AuthService authService;

    public RegisterHandler(AuthService authService){
        this.authService = authService;
    }

    public Object handle(Request req, Response res){
        try{
            RegisterRequest request = JsonUtils.fromJson(req, RegisterRequest.class);
            RegisterResult result = authService.register(request);
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(result);
        } catch(BadRequestException e){
            res.status(400);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        } catch(AlreadyTakenException e){
            res.status(403);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        } catch(Exception e){
            res.status(500);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }
    }
}
