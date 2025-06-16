package client;
import chess.ChessGame;
import ui.Session;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageObserver;

import java.util.Arrays;

public class LobbyClient implements Client {
    private final ServerFacade server;
    private final Session session;

    public LobbyClient(int port, Session session){
        ServerMessageObserver observer = new ServerMessageObserver() {
            @Override
            public void notify(ServerMessage serverMessage) {
            }
        };
        server = new ServerFacade(port, observer);
        this.session = session;
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
            switch (cmd){
                case "create" -> {
                    return create(params);
                }
                case "join" -> {
                    return join(params);
                }
                case "list" -> {
                    return list();
                }
                case "logout" -> {
                    return logout();
                }
                case "observe" -> {
                    return observe(params);
                }
                case "quit" -> {
                    return ClientState.EXIT;
                }
                default -> {
                    System.out.println("Unknown command. Type 'help' to view options.");
                    return ClientState.LOBBY;
                }
            }
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
            server.createGame(session.getAuthToken(), name);
            System.out.printf("Game created. Name: %s",name);
            return ClientState.LOBBY;
        }
        throw new Exception("Error: Please enter a name");
    }

    public ClientState join(String... params) throws Exception {
        // Validate number of arguments
        if(params.length < 2){
            throw new Exception("Error: Please enter a game ID and color <WHITE|BLACK>.");
        }

        // Validate ID
        int position;
        int id;
        try{
            position = Integer.parseInt(params[0]);
            updateGamesList();
            id = session.getGameId(position);
        } catch (Exception e){
            throw new Exception("Error in joining the game. Check availability by typing 'list'.");
        }

        // Validate color
        var color = params[1].toUpperCase();
        if(!color.equals("WHITE") && !color.equals("BLACK")){
            throw new Exception("Error: Invalid color. Please choose WHITE or BLACK.");
        }

        try{
            server.joinGame(session.getAuthToken(), color, id);
            session.setColor(color);
            session.setGame(new ChessGame()); // Implement functionality later
            System.out.printf("Joining game %s\n", session.getGameName(position));
            return ClientState.GAME;
        } catch(Exception e){
            throw new Exception("Error in joining the game. Check availability by typing 'list'.");
        }
    }

    private void updateGamesList() throws Exception {
        var games = server.listGames(session.getAuthToken()).games();
        session.setGamesList(games);
    }

    public ClientState list() throws Exception {
        var games = server.listGames(session.getAuthToken()).games();
        session.setGamesList(games);

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
            counter++;
        }
        System.out.print(result);
        return ClientState.LOBBY;
    }

    public ClientState logout() throws Exception {
        server.logout(session.getAuthToken());
        System.out.println("Logging out...");
        return ClientState.LOGIN;
    }

    public ClientState observe(String... params) throws Exception {
        // Validate number of arguments
        if(params.length < 1){
            throw new Exception("Error: Please enter a game ID.");
        }

        // Validate ID
        int position;
        int id;
        try{
            position = Integer.parseInt(params[0]);
            updateGamesList();
            id = session.getGameId(position);
        } catch (Exception e){
            throw new Exception("""
                    Error: Failed to join game.
                    Please check if the game number exists (use 'list' to see available games).
                    """);
        }
        try{
            server.joinGame(session.getAuthToken(), "OBSERVE", id);
            session.setColor(null);
            session.setGame(new ChessGame()); // Add functionality in Phase 6
            System.out.printf("Observing game %s\n", session.getGameName(position));
            return ClientState.GAME;
        }catch(Exception e){
            throw new Exception("""
                    Error: Failed to join game.
                    Please check if the game number exists (use 'list' to see available games).
                    """);
        }
    }

}
