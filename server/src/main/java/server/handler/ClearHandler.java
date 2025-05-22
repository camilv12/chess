package server.handler;
import spark.Request;
import service.ClearService;
import spark.Response;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    public Object handle(Request ignored, Response res){
        try{
            clearService.clear();
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(new Object());
        } catch (Exception e) {
            res.status(500);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }
    }


}
