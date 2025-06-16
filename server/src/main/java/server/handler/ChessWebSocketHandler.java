package server.handler;

import chess.ChessGame;
import model.JoinGameRequest;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.AuthService;
import service.GameService;
import service.JoinGameService;
import websocket.commands.JoinCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ChessWebSocketHandler {
    private static final Map<Session, Integer> sessions = new ConcurrentHashMap<>();
    private final AuthService authService;
    private final GameService gameService;
    private final JoinGameService joinGameService;

    public ChessWebSocketHandler(AuthService authService, GameService gameService, JoinGameService joinGameService) {
        this.authService = authService;
        this.gameService = gameService;
        this.joinGameService = joinGameService;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        sessions.put(session, null);
        try {
            session.getRemote().sendString("{\"serverMessageType\":\"CONNECTED\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        // Minimal message handling
        try {
            UserGameCommand command = JsonUtils.commandFromJson(message);
            if(command instanceof JoinCommand joinCmd){
                handleJoin(session, joinCmd);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleJoin(Session session, JoinCommand joinCmd) throws IOException {
        try{
            int id = joinCmd.getGameID();
            sessions.put(session, id);
            joinGameService.joinGame(new JoinGameRequest(
                    joinCmd.getAuthToken(),
                    joinCmd.getPlayerColor(),
                    joinCmd.getGameID()
            ));
            String username = authService.getUsername(joinCmd.getAuthToken());
            String notification = (joinCmd.getPlayerColor().equals("OBSERVE")) ?
                    JsonUtils.toJson(new NotificationMessage(username + " is now observing")) :
                    JsonUtils.toJson(new NotificationMessage(username + " joined as " + joinCmd.getPlayerColor()));
            broadcastToGame(id, notification);

            ChessGame game = JsonUtils.fromJson(gameService.getGame(joinCmd.getGameID()), ChessGame.class);
            session.getRemote().sendString(JsonUtils.toJson(
                    new LoadGameMessage(game)));

        } catch(Exception e){
            session.getRemote().sendString(JsonUtils.errorResponse(e.getMessage()));
        }


    }

    private void broadcastToGame(int gameID, String json) {
        for (Map.Entry<Session, Integer> entry : sessions.entrySet()) {
            Session session = entry.getKey();
            int sessionGameID = entry.getValue();

            if (sessionGameID == gameID && session.isOpen()) {
                try {
                    session.getRemote().sendString(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        sessions.remove(session);
        error.printStackTrace();
    }
}