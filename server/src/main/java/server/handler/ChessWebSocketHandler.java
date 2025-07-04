package server.handler;

import chess.*;
import model.AuthRequest;
import model.GameData;
import model.JoinGameRequest;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.AuthService;
import service.GameService;
import service.GameSessionService;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ChessWebSocketHandler {
    private static final Map<Session, Integer> sessions = new ConcurrentHashMap<>();
    private final AuthService authService;
    private final GameService gameService;
    private final GameSessionService gameSessionService;

    public ChessWebSocketHandler(){
        this(null, null, null);
    }

    public ChessWebSocketHandler(AuthService authService, GameService gameService, GameSessionService gameSessionService) {
        this.authService = authService;
        this.gameService = gameService;
        this.gameSessionService = gameSessionService;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        if (authService == null || gameService == null || gameSessionService == null) {
            System.err.println("WebSocket services not initialized!");
            session.close(1011, "Server configuration error");
            return;
        }

        sessions.put(session, 0);
        try {
            session.getRemote().sendString("{\"serverMessageType\":\"CONNECTED\"}");
        } catch (IOException e) {
            System.err.println("WebSocket connection error: " + e.getMessage());
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = JsonUtils.commandFromJson(message);
            if(command instanceof JoinCommand joinCmd){
                handleJoin(session, joinCmd);
            }
            else if(command instanceof MakeMoveCommand makeMoveCommand){
                handleMakeMove(session, makeMoveCommand);
            }
            else if(command instanceof LeaveCommand leaveCommand){
                handleLeave(session, leaveCommand);
            }
            else if(command instanceof ResignCommand resignCommand){
                handleResign(session, resignCommand);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleJoin(Session session, JoinCommand joinCmd) throws IOException {
        try{
            int id = joinCmd.getGameID();
            sessions.put(session, id);
            gameSessionService.joinGame(new JoinGameRequest(
                    joinCmd.getAuthToken(),
                    joinCmd.getPlayerColor(),
                    joinCmd.getGameID()
            ));
            String username = authService.getUsername(joinCmd.getAuthToken());
            String notification = joinCmd.getPlayerColor().equals("OBSERVE")
                    ? NotificationHandler.observerJoined(username)
                    : NotificationHandler.playerJoined(username, joinCmd.getPlayerColor());
            broadcastToGame(id, notification);

            ChessGame game = JsonUtils.fromJson(gameService.getGame(joinCmd.getGameID()), ChessGame.class);
            session.getRemote().sendString(JsonUtils.toJson(
                    new LoadGameMessage(game)));

        } catch(Exception e){
            session.getRemote().sendString(JsonUtils.errorResponse(e.getMessage()));
        }


    }

    private void handleMakeMove(Session session, MakeMoveCommand makeMoveCmd) throws IOException {
        try{
            // Validate command
            String token = makeMoveCmd.getAuthToken();
            authService.authenticate(new AuthRequest(token));
            String username = authService.getUsername(token);
            int id = makeMoveCmd.getGameID();
            ChessGame game = JsonUtils.fromJson(gameService.getGame(id), ChessGame.class);

            // Validate and execute move
            game.makeMove(makeMoveCmd.getMove());
            gameService.updateGame(id, game);

            // Send updates to all clients
            session.getRemote().sendString(JsonUtils.toJson(new LoadGameMessage(game)));
            broadcastToGame(id, JsonUtils.toJson(new LoadGameMessage(game)));
            String moveNotation = getMoveNotation(makeMoveCmd.getMove(), game);
            String notification = NotificationHandler.playerMoved(username, moveNotation);
            broadcastToGame(id, JsonUtils.toJson(new NotificationMessage(notification)));

            // Check for conditions
            if (game.isInCheckmate(game.getTeamTurn())) {
                String winner = getOpponentUsername(game, id);
                String checkmateMsg = NotificationHandler.checkmateAlert(winner);
                broadcastToGame(id, JsonUtils.toJson(new NotificationMessage(checkmateMsg)));
                gameService.endGame(id);
            } else if (game.isInCheck(game.getTeamTurn())) {
                String checkMsg = NotificationHandler.checkAlert(username);
                broadcastToGame(id, JsonUtils.toJson(new NotificationMessage(checkMsg)));
            } else if (game.isInStalemate(game.getTeamTurn())){
                broadcastToGame(id, JsonUtils.toJson(NotificationHandler.stalemateAlert()));
                gameService.endGame(id);
            }


        } catch (Exception e) {
            session.getRemote().sendString(JsonUtils.errorResponse(e.getMessage()));
        }
    }

    private String getMoveNotation(ChessMove move, ChessGame game) {
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
        String pieceChar = switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "";
        };

        return pieceChar + positionToNotation(move.getStartPosition()) +
                (game.getBoard().getPiece(move.getEndPosition()) != null ? "x" : "") +
                positionToNotation(move.getEndPosition());
    }

    private String positionToNotation(ChessPosition position) {
        return (char)('a' + position.getColumn() - 1) + "" + position.getRow();
    }

    private String getOpponentUsername(ChessGame game, int gameID) {
        GameData gameData = JsonUtils.fromJson(gameService.getGame(gameID), GameData.class);
        if (game.getTeamTurn() == ChessGame.TeamColor.WHITE) {
            // Black wins
            return gameData.blackUsername() != null ? gameData.blackUsername() : "Black";
        } else {
            // White wins
            return gameData.whiteUsername() != null ? gameData.whiteUsername() : "White";
        }
    }

    private void handleLeave(Session session, LeaveCommand leaveCmd) throws IOException {
        try{
            // Validate
            authService.authenticate(new AuthRequest(leaveCmd.getAuthToken()));
            String username = authService.getUsername(leaveCmd.getAuthToken());
            int id = leaveCmd.getGameID();
            // Execute
            gameSessionService.leaveGame(leaveCmd.getAuthToken(), leaveCmd.getGameID());
            sessions.put(session, 0);

            // Notify
            String notification = NotificationHandler.playerLeft(username);
            broadcastToGame(id, JsonUtils.toJson(
                            new NotificationMessage(notification)));
            session.close();
        }catch(Exception e){
            session.getRemote().sendString(JsonUtils.errorResponse(e.getMessage()));
        }
    }

    private void handleResign(Session session, ResignCommand resignCmd) throws IOException {
        try {
            // Validate
            authService.authenticate(new AuthRequest(resignCmd.getAuthToken()));
            String username = authService.getUsername(resignCmd.getAuthToken());
            int gameID = resignCmd.getGameID();

            // Execute
            gameSessionService.resignGame(username, gameID);

            // Notify
            String notification = NotificationHandler.playerResigned(username);
            broadcastToGame(gameID, JsonUtils.toJson(
                    new NotificationMessage(notification)));

            // Optional
            session.close();

        } catch (Exception e) {
            session.getRemote().sendString(JsonUtils.errorResponse(e.getMessage()));
        }
    }

    private void broadcastToGame(int gameID, String json) {
        for (Map.Entry<Session, Integer> entry : sessions.entrySet()) {
            Session session = entry.getKey();
            int sessionGameID = entry.getValue();

            if (sessionGameID == gameID && sessionGameID > 0 && session.isOpen()) {
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
        System.err.printf("WebSocket closed - Session: %s, Status: %d, Reason: %s%n",
                session.getRemoteAddress(), statusCode, reason);
        sessions.remove(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.printf("WebSocket error - Session: %s, Error: %s%n",
                session.getRemoteAddress(), error.getMessage());
        sessions.remove(session);
        error.printStackTrace();
    }
}