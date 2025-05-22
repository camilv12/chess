package server.handler;
import spark.Request;
import service.RegisterService;
import spark.Response;

public class RegisterHandler {
    private final RegisterService registerService;

    public RegisterHandler(RegisterService registerService){
        this.registerService = registerService;
    }

}
