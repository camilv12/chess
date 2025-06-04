package client;
import java.util.Arrays;

public class LobbyClient implements Client {
    private final ServerFacade server;
    private String authToken;
    
    public LobbyClient(int port){
        server = new ServerFacade(port);
        authToken = "";
    }
    
    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    @Override
    public String prompt() {
        return "[LOBBY] >>> ";
    }

    @Override
    public ClientState eval(String input) throws Exception {
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
                default -> ClientState.LOBBY;
            };
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void help(){
        System.out.print("""
               Lobby Menu:
               help - display menu
               create <NAME> - create a new game
               join <ID> [WHITE|BLACK] - join an existing game
               list - lists all games
               logout - log out of this session
               quit - exit program
               """);
    }

    public ClientState create(String... params) throws Exception {
        if(params.length >= 1){
            var name = params[0];
            var id = server.createGame(authToken, name).gameID();
            System.out.printf("Game created. %s, ID: %d",name,id);
            return ClientState.LOBBY;
        }
        throw new Exception("Error: Please enter a name");
    }

    public ClientState join(String... params) throws Exception {
        if(params.length >= 2){
            int id = Integer.parseInt(params[0]);
            var color = params[1].toUpperCase();
            server.joinGame(authToken, color, id);
            System.out.printf("Joining game %d", id);
            return ClientState.GAME;
        }
        throw new Exception("Error: Please enter a game ID and color <WHITE|BLACK>.");
    }

    public ClientState list() throws Exception {
        var games = server.listGames(authToken).games();

        if(games.isEmpty()){
            System.out.println("No games available. Create one by typing 'create <NAME>'!");
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
        System.out.print(result);
        return ClientState.LOBBY;
    }

    public ClientState logout() throws Exception {
        server.logout(authToken);
        System.out.println("Logging out...");
        return ClientState.LOGIN;
    }

    public ClientState observe(String... params) throws Exception {
        if(params.length >= 1){
            int id = Integer.parseInt(params[0]);
            server.joinGame(authToken, null, id);
            System.out.printf("Observing game %d", id);
            return ClientState.GAME;
        }
        throw new Exception("Error: Please enter a game ID.");
    }

}
