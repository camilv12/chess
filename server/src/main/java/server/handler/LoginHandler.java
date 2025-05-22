package server.handler;

import service.LoginService;

public class LoginHandler {
    private final LoginService loginService;

    public LoginHandler(LoginService loginService){
        this.loginService = loginService;
    }
}
