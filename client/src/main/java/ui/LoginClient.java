package ui;

import client.ServerFacade;
import service.UnauthorizedException;

import java.util.Arrays;

public class LoginClient implements Client {
    private final ServerFacade server;
    private final Session session;

    public LoginClient(int port, Session session){
        server = new ServerFacade(port);
        this.session = session;
    }

    @Override
    public String prompt() {
        return "[LOGIN] >>> ";
    }

    @Override
    public ClientState eval(String input) throws Exception {
        try{
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
             switch (cmd){
                case "login" -> {
                    return login(params);
                }
                case "register" -> {
                    return register(params);
                }
                case "quit" ->{
                    return ClientState.EXIT;
                }
                default -> {
                    System.out.println("Unknown command. Type 'help' to view options.");
                    return ClientState.LOGIN;
                }
            }
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void help(){
        System.out.print("""
               Login Menu:
               help - display menu
               login <USERNAME> <PASSWORD> - log into your account
               register <USERNAME> <PASSWORD> <EMAIL> - create a new account
               quit - exit program
               """);
    }

    public ClientState login(String... params) throws Exception {
        try{
            if(params.length >= 2) {
                var username = params[0];
                var password = params[1];
                var result = server.login(username, password);
                session.setUsername(result.username());
                session.setAuthToken(result.authToken());
                System.out.printf("Logged in as %s\n", session.getUsername());
                return ClientState.LOBBY;
            }
            throw new Exception("Error: Please enter username and password");
        } catch(UnauthorizedException e){
            throw new Exception("Incorrect username or password");
        }
    }

    public ClientState register(String... params) throws Exception {
        if(params.length >= 3){
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var result = server.register(username, password, email);
            session.setUsername(result.username());
            session.setAuthToken(result.authToken());
            System.out.printf("Registration successful. Logged in as %s\n", session.getUsername());
            return ClientState.LOBBY;
        }
        throw new Exception("Error: Please enter username, password, and email");
    }
}
