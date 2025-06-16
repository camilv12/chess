package client;
import chess.ChessMove;
import client.websocket.HttpCommunicator;
import client.websocket.WebSocketCommunicator;
import com.google.gson.Gson;
import model.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessageObserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

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
            websocket.connect();
        }

        // Send command
        websocket.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, token, id));

    }

    public void connectWebSocket(){
        if(!websocket.isConnected()){
            websocket.connect();
        }
    }

    public void observeGame(String token, int id){
        websocket.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, token, id));
    }

    public void makeMove(String authToken, int gameID, ChessMove move){
        websocket.send(new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID));
    }

    public void resign(String authToken, int gameID){
        websocket.send(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }

    public void leaveWebSocket(String authToken, int gameID){
        websocket.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void disconnectWebSocket(){
        if(websocket.isConnected()){
            websocket.disconnect();
        }
    }

    public void clear() throws Exception {
        httpCommunicator.delete("/db", null);
    }

    private void checkStatus(int code) throws Exception {
        switch(code){
            case 400 -> throw new Exception("Error: Invalid Request");
            case 401 -> throw new Exception("Error: Unauthorized");
            case 403 -> throw new Exception("Error: Already Taken");
            case 500 -> throw new Exception("Error: Connection failed");
        }
    }
}
