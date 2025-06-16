package server.handler;

import chess.*;
import model.AuthRequest;
import model.JoinGameRequest;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.AuthService;
import service.GameService;
import service.JoinGameService;
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
        try {
            UserGameCommand command = JsonUtils.commandFromJson(message);
            if(command instanceof JoinCommand joinCmd){
                handleJoin(session, joinCmd);
            }
            else if(command instanceof MakeMoveCommand makeMoveCommand){
                handleMakeMove(session, makeMoveCommand);
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
            String notification = username + " moved " + moveNotation;
            broadcastToGame(id, JsonUtils.toJson(new NotificationMessage(notification)));

            // Check for conditions
            if (game.isInCheckmate(game.getTeamTurn())) {
                String checkmateMsg = "Checkmate! " + username + " wins!";
                broadcastToGame(id, JsonUtils.toJson(new NotificationMessage(checkmateMsg)));
            } else if (game.isInCheck(game.getTeamTurn())) {
                String checkMsg = username + " is in check";
                broadcastToGame(id, JsonUtils.toJson(new NotificationMessage(checkMsg)));
            } else if (game.isInStalemate(game.getTeamTurn())){
                broadcastToGame(id, JsonUtils.toJson(new NotificationMessage("Stalemate")));
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