package client;

import chess.ChessGame;
import ui.ChessBoardRenderer;

public class GameClient implements Client {
    private boolean isWhite = true;
    private final ChessGame game;

    public GameClient(ChessGame game){
        this.game = game;
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
            return switch(cmd){
                case "redraw" -> draw();
                case "leave" -> leave();
                default -> ClientState.GAME;
            };
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void help(){
        System.out.print( """
               Game Menu:
               help - display menu
               redraw - redraw board
               leave - leave game
               quit - exit program
               """);
    }

    public ClientState leave(){
        return ClientState.LOBBY;
    }

    public void setPerspective(String color){
        isWhite = (color.equals("WHITE"));
    }

    public ClientState draw(){
        ChessBoardRenderer.render(game, isWhite);
        return ClientState.GAME;
    }


}