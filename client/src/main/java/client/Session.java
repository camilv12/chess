package client;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    private String username;
    private String authToken;
    private ChessGame game;
    private String color;
    private Collection<GameData> gamesList;
    private final ConcurrentHashMap<Integer, GameData> gamesMap = new ConcurrentHashMap<>();

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

    public void setGamesList(Collection<GameData> gamesList){
        this.gamesList = gamesList;
    }

    public Integer getGameId(int position){
        updateMap();
        return gamesMap.get(position).gameID();
    }

    public String getGameName(int position){
        updateMap();
        return gamesMap.get(position).gameName();
    }

    private void updateMap(){
        if(gamesList.isEmpty()){
            return;
        }
        int counter = 1;
        for(GameData game: gamesList){
            gamesMap.put(counter, game);
            counter++;
        }
    }

}
