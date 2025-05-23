package server.handler;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.model.RegisterRequest;
import service.model.RegisterResult;
import spark.Request;
import service.RegisterService;
import spark.Response;

public class RegisterHandler {
    private final RegisterService registerService;

    public RegisterHandler(RegisterService registerService){
        this.registerService = registerService;
    }

    public Object handle(Request req, Response res){
        try{
            RegisterRequest request = JsonUtils.fromJson(req, RegisterRequest.class);
            RegisterResult result = registerService.register(request);
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
