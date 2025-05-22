package server.handler;
import spark.Request;
import service.ClearService;
import spark.Response;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }


}
