package server.handler;

import service.LogoutService;

public class LogoutHandler {
    private final LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService){
        this.logoutService = logoutService;
    }
}
