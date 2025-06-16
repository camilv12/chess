package client;


import chess.*;
import ui.ChessBoardRenderer;
import ui.Session;
import websocket.messages.*;

import java.util.Arrays;


public class GameClient implements Client {
    private final Session session;
    private final ServerFacade server;

    public GameClient(int port, Session session){
        ServerMessageObserver observer = new ServerMessageObserver() {
            @Override
            public void onNotification(NotificationMessage message) {
                System.out.println("\n"  + message.getMessage() + "\n" + prompt());
            }

            @Override
            public void onError(ErrorMessage message) {
                System.err.println("\n"  + message.getErrorMessage() + "\n" + prompt());
            }

            @Override
            public void onGameUpdate(LoadGameMessage message) {
                session.setGame(message.getGame());
                redraw();
            }
        };
        server = new ServerFacade(port, observer);
        this.session = session;
    }

    @Override
    public String prompt() {
        return "[GAME] >>> ";
    }

    @Override
    public ClientState eval(String input) throws Exception{
        try{
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch(cmd){
                case "redraw" -> {
                    return redraw();
                }
                case "highlight" -> {
                    return highlight(params);
                }
                case "move" -> {
                    try{
                        makeMove(params);
                        return ClientState.GAME;
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                        return ClientState.GAME;
                    }
                }
                case "resign" -> {
                    return resign();
                }
                case "leave" -> {
                    return leave();
                }
                case "quit" ->{
                    return ClientState.EXIT;
                }
                default -> {
                    System.out.println("Unknown command. Type 'help' to view options.");
                    return ClientState.GAME;
                }
            }
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void help(){
        System.out.print( """
                Game Menu:
                help - display menu
                highlight <POSITION> - highlights legal moves for a piece
                move <FROM> <TO> [PROMOTION] - make a move (e.g. move e2 e4)
                redraw - redraw board
                resign - resign from current game
                leave - leave current game
                quit - exit program

                Movement notation:
                - Positions use algebraic notation (e.g. e2, a1, h8)
                - Optional promotion piece (Q/R/B/N) for pawn promotions
               """);
    }

    public ClientState leave(){
        server.leave(session.getAuthToken(), session.getGameID());
        System.out.println("Left the game");
        return ClientState.LOBBY;
    }

    public ClientState resign(){
        server.resign(session.getAuthToken(), session.getGameID());
        System.out.println("You have resigned the game");
        return ClientState.LOBBY;
    }

    public ClientState highlight(String... params){
        // Validate
        if(params.length < 1){
            throw new IllegalArgumentException("Error: Please enter a position");
        }
        var position = parsePosition(params[0]);
        ChessPiece piece = session.getGame().getBoard().getPiece(position);
        if (piece == null || piece.getTeamColor() != session.getGame().getTeamTurn()) {
            System.out.println("No valid piece at this position");
            return ClientState.GAME;
        }
        boolean isWhitePerspective = (session.getColor() == null) || (session.getColor().equals("WHITE"));
        ChessBoardRenderer.render(session.getGame(), isWhitePerspective, position);
        return ClientState.GAME;
    }

    public ClientState redraw(){
        boolean isWhitePerspective = (session.getColor() == null) || (session.getColor().equals("WHITE"));
        ChessBoardRenderer.render(session.getGame(), isWhitePerspective, null);
        return ClientState.GAME;
    }

    private void makeMove(String... params) {
        if(params.length < 2){
            throw new IllegalArgumentException("Error: Please enter two positions and a promotion (if applicable)");
        }
        String fromPos = params[0];
        String toPos = params[1];
        String promotion = params.length >= 3 ? params[2] : null;
        try {
            ChessPosition from = parsePosition(fromPos);
            ChessPosition to = parsePosition(toPos);
            ChessMove move = new ChessMove(from, to,
                    promotion != null ? parsePromotion(promotion) : null);

            String moveDescription = getMoveDescription(session.getGame(), move);

            server.makeMove(
                    session.getAuthToken(),
                    session.getGameID(),
                    move
            );
            System.out.println("Making move: " + moveDescription);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid move: " + e.getMessage());
        }
    }

    private String getMoveDescription(ChessGame game, ChessMove move) {
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
        String pieceName = getPieceName(piece);
        String capture = game.getBoard().getPiece(move.getEndPosition()) != null ? "x" : "";
        return pieceName + positionToNotation(move.getStartPosition()) +
                capture + positionToNotation(move.getEndPosition()) +
                (move.getPromotionPiece() != null ? "=" + getPieceName(new ChessPiece(piece.getTeamColor(), move.getPromotionPiece())) : "");
    }

    private ChessPosition parsePosition(String pos) {
        if (pos.length() != 2) {
            throw new IllegalArgumentException("Position must be 2 characters (e.g. e4)");
        }
        char file = pos.charAt(0);
        char rank = pos.charAt(1);

        if (file < 'a' || file > 'h') {
            throw new IllegalArgumentException("File must be between a-h");
        }
        if (rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Rank must be between 1-8");
        }

        return new ChessPosition(
                Character.getNumericValue(rank),
                file - 'a' + 1
        );
    }

    private ChessPiece.PieceType parsePromotion(String promo) {
        return switch (promo.toUpperCase()) {
            case "Q" -> ChessPiece.PieceType.QUEEN;
            case "R" -> ChessPiece.PieceType.ROOK;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "N" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new IllegalArgumentException(
                    "Promotion must be Q (queen), R (rook), B (bishop), or N (knight)");
        };
    }

    private String getPieceName(ChessPiece piece) {
        return switch(piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "";
        };
    }

    private String positionToNotation(ChessPosition position) {
        return (char)('a' + position.getColumn() - 1) + "" + position.getRow();
    }




}