package websocket.commands;

import chess.ChessMove;

public class JoinCommand extends UserGameCommand{
    private final String playerColor;

    public JoinCommand(String authToken, Integer gameID, String playerColor){
        super(CommandType.CONNECT, authToken, gameID);
        this.playerColor = playerColor;
    }

    public String getPlayerColor(){
        return this.playerColor;
    }
}
