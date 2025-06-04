package client;

import chess.ChessGame;
import ui.ChessBoardRenderer;

public class GameClient {
    private boolean isWhite = true;
    private final ChessGame game;

    public GameClient(ChessGame game){
        this.game = game;
    }

    public String eval(String input){
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        if(cmd.equals("redraw")){
            return draw();
        }
        else{
            return help();
        }
    }

    public String help(){
        return """
               Game Menu:
               help - display menu
               redraw - redraw board
               quit - exit program
               """;
    }

    public void setPerspective(String color){
        isWhite = (color.equals("WHITE"));
    }

    public String draw(){
        ChessBoardRenderer.render(game, isWhite);
        return "Drawing Board";
    }


}