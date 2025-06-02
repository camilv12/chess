package client;
import java.util.Arrays;

public class LobbyClient {
    private final ServerFacade server;
    private final String authToken;

    public LobbyClient(String token, int port){
        this.authToken = token;
        server = new ServerFacade(port);
    }

    public String eval(String input){
        try{
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd){
                case "create" -> create(params);
                case "join" -> join(params);
                case "list" -> list();
                case "logout" -> logout();
                case "observe" -> observe(params);
                default -> help();
            };
        } catch(Exception e){
            return e.getMessage();
        }
    }

    public String help(){
        return """
               Lobby Menu:
               help - display menu
               create <NAME> - create a new game
               join <ID> [WHITE|BLACK] - join an existing game
               list - lists all games
               logout - log out of this session
               quit - exit program
               """;
    }

    public String create(String... params) throws Exception {
        if(params.length >= 1){
            var name = params[0];
            var id = server.createGame(authToken, name).gameID();
            return String.format("Game created. %s, ID: %d",name,id);
        }
        throw new Exception("Error: Please enter a name");
    }

    public String join(String... params) throws Exception {
        if(params.length >= 2){
            int id = Integer.parseInt(params[0]);
            var color = params[1].toUpperCase();
            server.joinGame(authToken, color, id);
            return String.format("Joining game %d", id);
        }
        throw new Exception("Error: Please enter a game ID and color <WHITE|BLACK>.");
    }

    public String list() throws Exception {
        var games = server.listGames(authToken).games();

        if(games.isEmpty()){
            return "No games available. Create one by typing 'create <NAME>'!";
        }

        var result = new StringBuilder();
        int counter = 1;
        for(var game : games){
            result.append(String.format("%d. %s (White: %s, Black: %s)\n",
                    counter,
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "[Empty]",
                    game.blackUsername() != null ? game.blackUsername() : "[Empty]"
            ));
        }
        return result.toString();
    }

    public String logout() throws Exception {
        server.logout(authToken);
        return "Logging out";
    }

    public String observe(String... params) throws Exception {
        if(params.length >= 1){
            int id = Integer.parseInt(params[0]);
            server.joinGame(authToken, null, id);
            return String.format("Observing game %d", id);
        }
        throw new Exception("Error: Please enter a game ID.");
    }

}
