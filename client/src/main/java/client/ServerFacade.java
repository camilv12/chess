package client;
import chess.ChessMove;
import client.websocket.HttpCommunicator;
import client.websocket.WebSocketCommunicator;
import exception.CommunicationException;
import model.*;
import websocket.commands.JoinCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessageObserver;

/**
 * A class that encapsulates HTTP requests to the server and results from the server.
 * This class handles the communication between the client and the server.
 */

public class ServerFacade {
    private final WebSocketCommunicator websocket;
    private final HttpCommunicator httpCommunicator;

    public ServerFacade(int port, ServerMessageObserver observer) {
        try{
            String serverUrl = "http://localhost:" + port;
            this.httpCommunicator = new HttpCommunicator(serverUrl);
            this.websocket = new WebSocketCommunicator(serverUrl, observer);
        } catch(Exception e){
            throw new RuntimeException("Server connection failed");
        }
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        return httpCommunicator.post("/session", null, request, LoginResult.class);
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);
        return httpCommunicator.post("/user", null, request, RegisterResult.class);
    }

    public void logout(String token) throws Exception{
        httpCommunicator.delete("/session", token);
    }

    public CreateGameResult createGame(String token, String name) throws Exception{
        CreateGameRequest request = new CreateGameRequest(name);
        return httpCommunicator.post("/game",token,request,CreateGameResult.class);
    }

    public ListGamesResult listGames(String token) throws Exception{
        return httpCommunicator.get("/game", token, ListGamesResult.class);
    }

    public void joinGame(String token, String color, int id) throws Exception{
        // HTTP Join
        JoinGameRequest request = new JoinGameRequest(token, color, id);
        httpCommunicator.put("/game", token, request, Void.class);

        // Connect websocket
        if(!websocket.isConnected()){
            try{
                websocket.connect();
                websocket.send(new JoinCommand(token, id, color));
            }catch(CommunicationException e){
                if(e.shouldReconnect()){
                    websocket.reconnect();
                }
                else{
                    throw e;
                }
            }
        }

    }

    public void connectWebSocket(){
        if(!websocket.isConnected()){
            try{
                websocket.connect();
            } catch (CommunicationException e) {
                if(e.shouldReconnect()){
                    websocket.reconnect();
                }
                else{
                    throw e;
                }
            }
        }
    }

    public void observeGame(String token, int id){
        websocket.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, token, id));
    }

    public void makeMove(String authToken, int gameID, ChessMove move){
        websocket.send(new MakeMoveCommand(authToken, gameID, move));
    }

    public void resign(String authToken, int gameID){
        websocket.send(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }

    public void leave(String authToken, int gameID){
        websocket.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void disconnectWebSocket(){
        if(websocket.isConnected()){
            try{
                websocket.disconnect();
            } catch (CommunicationException e) {
                if(e.shouldReconnect()){
                    websocket.reconnect();
                } else{
                    throw e;
                }
            }
        }
    }

    public void clear() throws Exception {
        httpCommunicator.delete("/db", null);
    }
}
