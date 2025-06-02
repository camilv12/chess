package client;

import java.util.Arrays;

public class LoginClient {
    private final ServerFacade server;

    public LoginClient(int port){
        server = new ServerFacade(port);
    }

    public String eval(String input){
        try{
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd){
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch(Exception e){
            return e.getMessage();
        }
    }

    public String help(){
        return """
               Login Menu:
               help - display menu
               login <USERNAME> <PASSWORD> - log into your account
               register <USERNAME> <PASSWORD> <EMAIL> - create a new account
               quit - exit program
               """;
    }

    public String login(String... params) throws Exception {
        if(params.length >= 2) {
            var username = params[0];
            var password = params[1];
            var result = server.login(username, password);
            return String.format("Logged in as %s", result.username());
        }
        throw new Exception("Error: Please enter username and password");
    }

    public String register(String... params) throws Exception {
        if(params.length >= 3){
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var result = server.register(username, password, email);
            return String.format("Registration successful. Logged in as %s", result.username());
        }
        throw new Exception("Error: Please enter username, password, and email");
    }
}
