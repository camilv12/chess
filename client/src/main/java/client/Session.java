package client;

import chess.ChessGame;

public class Session {
    private String username;
    private String authToken;
    private ChessGame game;
    private String color;

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public void setGame(ChessGame game){
        this.game = game;
    }

    public void setColor(String color){
        this.color = color;
    }

    public String getColor(){
        return this.color;
    }

}
