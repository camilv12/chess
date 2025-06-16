package server.handler;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import service.GameService;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChessWebSocketHandler extends WebSocketHandler {
    private final GameService gameService;
    private final Map<Integer, Map<String, Session>> gameSessions = new HashMap<>();
    private final Gson gson = new Gson();

    public ChessWebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator((req, resp) -> new ChessWebSocket());
    }

    @WebSocket
    public class ChessWebSocket {
        private Session session;
        private String authToken;
        private Integer gameID;

        @OnWebSocketConnect
        public void onConnect(Session session) {
            this.session = session;
        }

        @OnWebSocketMessage
        public void onMessage(Session session, String message) throws IOException {
            try {
                UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
                this.authToken = command.getAuthToken();
                this.gameID = command.getGameID();

                switch (command.getCommandType()) {
                    case CONNECT -> handleConnect();
                    case MAKE_MOVE -> handleMakeMove(gson.fromJson(message, MakeMoveCommand.class));
                    case LEAVE -> handleLeave();
                    case RESIGN -> handleResign();
                }
            } catch (Exception ex) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
            }
        }

        private void handleConnect() throws IOException {
            addSessionToGame();
            session.getRemote().sendString(gson.toJson(new LoadGameMessage(gameService.getGame(gameID))));
        }

        private void handleMakeMove(MakeMoveCommand command) throws IOException {
            ChessGame game = gameService.getGame(gameID);
            // Validate move and update game
            broadcastGameState();
        }

        private void handleLeave() throws IOException {
            removeSessionFromGame();
            session.close();
        }

        private void handleResign() throws IOException {
            // Mark game as resigned
            broadcastNotification(authToken + " resigned from the game");
        }

        private void addSessionToGame() {
            gameSessions.computeIfAbsent(gameID, k -> new HashMap<>()).put(authToken, session);
        }

        private void removeSessionFromGame() {
            if (gameID != null && authToken != null) {
                Map<String, Session> game = gameSessions.get(gameID);
                if (game != null) {
                    game.remove(authToken);
                }
            }
        }

        private void broadcastGameState() throws IOException {
            ChessGame game = gameService.getGame(gameID);
            Map<String, Session> sessions = gameSessions.get(gameID);
            if (sessions != null) {
                for (Session s : sessions.values()) {
                    if (s.isOpen()) {
                        s.getRemote().sendString(gson.toJson(new LoadGameMessage(game)));
                    }
                }
            }
        }

        private void broadcastNotification(String message) throws IOException {
            Map<String, Session> sessions = gameSessions.get(gameID);
            if (sessions != null) {
                for (Session s : sessions.values()) {
                    if (s.isOpen()) {
                        s.getRemote().sendString(gson.toJson(new NotificationMessage(message)));
                    }
                }
            }
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            removeSessionFromGame();
        }

        @OnWebSocketError
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }
}