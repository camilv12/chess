package exception;

import java.io.IOException;

public class HttpException extends IOException {
    private final int status;
    public HttpException(int status, String message) {
        super("HTTP " + status + ":" + message);
        this.status = status;

    }
    public int getStatusCode(){
        return status;
    }
}
