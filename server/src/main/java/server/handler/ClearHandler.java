package server.handler;
import service.model.ClearRequest;
import service.model.ClearResult;
import spark.Request;
import service.ClearService;
import spark.Response;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    public Object handle(Request req, Response res){
        try{
            ClearRequest request = JsonUtils.fromJson(req, ClearRequest.class);
            ClearResult result = clearService.clear(request);
            res.status(200);
            res.type("application/json");
            return JsonUtils.toJson(result);
        } catch (Exception e) {
            res.status(500);
            return JsonUtils.errorResponse("Error: " + e.getMessage());
        }
    }


}
